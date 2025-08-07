package me.maxhub.logger.logback.encoder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Tracer;
import lombok.Setter;
import me.maxhub.logger.encoder.MessageEncoder;
import me.maxhub.logger.encoder.impl.JsonEncoder;
import me.maxhub.logger.encoder.impl.XmlEncoder;
import me.maxhub.logger.headers.impl.DetailedHeadersProvider;
import me.maxhub.logger.logback.encoder.model.LogModel;
import me.maxhub.logger.logback.encoder.model.Tracing;
import me.maxhub.logger.mask.DataMasker;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.properties.provider.PropertyProvider;
import me.maxhub.logger.mask.impl.*;
import me.maxhub.logger.properties.provider.impl.FilePropertyProvider;
import me.maxhub.logger.properties.provider.impl.SpringPropertyProvider;
import me.maxhub.logger.spring.SpringContextHolder;
import me.maxhub.logger.util.LoggingConstants;
import me.maxhub.logger.util.MessageLifecycle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.event.KeyValuePair;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 *
 */
public class MaskingJsonEncoder extends EncoderBase<ILoggingEvent> {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final String UNKNOWN = "unknown";
    private static final String PROJECT_NAME_ENV = "PROJECT_NAME";
    private static final String POD_SOURCE_ENV = "HOSTNAME";

    private final ObjectMapper mapper;
    private final JsonEncoder jsonEncoder;
    private final XmlEncoder xmlEncoder;

    private PropertyProvider propertyProvider = new FilePropertyProvider();
    private JsonMasker jsonMasker;
    private XmlMasker xmlMasker;
    private DataMasker defaultDataMasker;

    private Tracer tracer;

    private String projectName = UNKNOWN;
    private String podSource = UNKNOWN;

    /**
     * A configurable property that enables or disables the masking feature.
     * <p>Can be configured from the `logback.xml` file as following:
     * <pre>
     * {@code
     *     <import class="me.maxhub.logging.logback.encoder.MaskingJsonEncoder"/>
     *     <appender name="..." class="...">
     *         <encoder class="MaskingJsonEncoder">
     *             ...
     *             <enableMasker>false</enableMasker>
     *             ...
     *         </encoder>
     *     </appender>
     * }
     * </pre>
     */
    @Setter
    private Boolean enableMasker;
    /**
     * A configurable property that configures the default {@link DataMasker}.
     * <p>Can be configured from the `logback.xml` file as following:
     * <pre>
     * {@code
     *     <import class="me.maxhub.logging.logback.encoder.MaskingJsonEncoder"/>
     *     <appender name="..." class="...">
     *         <encoder class="MaskingJsonEncoder">
     *             ...
     *             <defaultMasker>json</defaultMasker>
     *             ...
     *         </encoder>
     *     </appender>
     * }
     * </pre>
     * <p>Allowed values are: {@code json} and {@code xml}
     */
    @Setter
    private String defaultMasker;
    /**
     * A configurable property that configures the {@link PropertyProvider} for logging properties.
     * <p>Can be configured from the `logback.xml` file as following:
     * <pre>
     * {@code
     *     <import class="me.maxhub.logging.logback.encoder.MaskingJsonEncoder"/>
     *     <appender name="..." class="...">
     *         <encoder class="MaskingJsonEncoder">
     *             ...
     *             <propertiesProvider>spring</propertiesProvider>
     *             ...
     *         </encoder>
     *     </appender>
     * }
     * </pre>
     * Allowed values are: {@code spring}. Anything else will default to {@link FilePropertyProvider}
     */
    @Setter
    private String propertiesProvider;

    public MaskingJsonEncoder() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        this.mapper = objectMapper;
        this.jsonEncoder = new JsonEncoder(objectMapper);
        this.xmlEncoder = new XmlEncoder();

        try {
            this.projectName = System.getenv(PROJECT_NAME_ENV);
            this.podSource = System.getenv(POD_SOURCE_ENV);
        } catch (Exception e) {
            addWarn("could not initialize projectName and podSource", e);
        }
    }

    @Override
    public void start() {
        initMasker();
        started = true;
    }

    @Override
    public byte[] headerBytes() {
        return EMPTY_BYTES;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        var logModel = LogModel.builder()
            .projectName(projectName)
            .podSource(podSource)
            .timestamp(event.getInstant())
            .logMessage(event.getFormattedMessage())
            .logLevel(event.getLevel().toString())
            .message(event.getFormattedMessage())
            .tracing(buildTracing());
        fillWithProperties(logModel, event);
        // todo can we determine the approximate log size?
        var baos = new ByteArrayOutputStream();
        try {
            mapper.writer().writeValues(baos).write(logModel.build());
            baos.write(LINE_SEPARATOR);
        } catch (Throwable t) {
            addError("Cannot encode log event", t);
        }
        return baos.toByteArray();
    }

    @Override
    public byte[] footerBytes() {
        return EMPTY_BYTES;
    }

    private Tracing buildTracing() {
        initTracer();
        if (Objects.nonNull(tracer) && Objects.nonNull(tracer.currentSpan())) {
            var context = Objects.requireNonNull(tracer.currentSpan()).context();
            return Tracing.builder()
                .traceId(context.traceId())
                .spanId(context.spanId())
                .parentId(context.parentId())
                .build();
        }
        return null;
    }

    private void initMasker() {
        if ("spring".equalsIgnoreCase(propertiesProvider)) {
            propertyProvider = new SpringPropertyProvider();
        }
        jsonMasker = new JsonMasker(mapper, propertyProvider);
        xmlMasker = new XmlMasker();
        // use JSON masker as default if 'defaultMasker' property is not provided.
        defaultDataMasker = jsonMasker;
        MessageEncoder<?> messageEncoder = jsonEncoder;
        if (defaultMasker != null && defaultMasker.equalsIgnoreCase(MaskerType.XML.name())) {
            defaultDataMasker = new XmlMasker();
            messageEncoder = xmlEncoder;
        }

        if (Boolean.FALSE.equals(enableMasker)) {
            defaultDataMasker = new NOPMasker();
            // this is needed so that the message body will be encoded either in a JSON or XML instead of `messageBody.toString()`
            ((NOPMasker) defaultDataMasker).setMessageEncoder(messageEncoder);
            addInfo("Data masker not enabled");
        }
    }

    private void initTracer() {
        try {
            if (Objects.isNull(tracer)) {
                tracer = SpringContextHolder.getBean(Tracer.class);
            }
        } catch (Exception e) {
            addWarn("could not initialize tracer", e);
        }
    }

    private void fillWithProperties(LogModel.LogModelBuilder builder, ILoggingEvent event) {
        var dataMasker = defaultDataMasker;
        KeyValuePair headersKvp = null;
        Object messageBody = null;
        var messageLifecycle = MessageLifecycle.ACTION;
        String automatedSystem = null;
        String opName = null;
        var keyValuePairs = event.getKeyValuePairs();
        if (Objects.nonNull(keyValuePairs)) {
            for (var kvp : keyValuePairs) {
                switch (kvp.key) {
                    case LoggingConstants.MESSAGE_BODY -> messageBody = kvp.value;
                    case LoggingConstants.HEADERS -> headersKvp = kvp;
                    case LoggingConstants.OP_NAME -> opName = getString(kvp);
                    case LoggingConstants.BODY_TYPE -> dataMasker = getMasker(kvp, defaultDataMasker);
                    case LoggingConstants.MSG_LIFECYCLE -> messageLifecycle = getMessageLifecycle(kvp);
                    case LoggingConstants.AS -> automatedSystem = getString(kvp);
                    case LoggingConstants.STATUS -> builder.status(getStatus(kvp, event));
                }
            }
        }

        var headersProvider = new DetailedHeadersProvider(propertyProvider, headersKvp);
        var headers = headersProvider.getHeaders();
        builder.headers(headers);

        var informationSB = new StringBuilder(messageLifecycle.getPrefix());
        if (StringUtils.isNoneBlank(automatedSystem)) {
            informationSB
                .append(": ")
                .append(automatedSystem);
        }
        builder.information(informationSB.toString());
        builder.rqUID(MDC.get(LoggingConstants.RQ_UID));
        if (StringUtils.isBlank(opName)) {
            opName = MDC.get(LoggingConstants.OP_NAME);
        }
        builder.operationName(opName);

        // do masking only after `dataMasker` is initialized based on kvp
        if (Objects.nonNull(messageBody)) {
            try {
                messageBody = dataMasker.mask(messageBody);
            } catch (Exception e) {
                addWarn("could not mask messageBody", e);
                messageBody = e.getMessage();
            }
            builder.message(messageBody);
        }
    }

    private DataMasker getMasker(KeyValuePair kvp, DataMasker defaultDataMasker) {
        MessageEncoder<?> messageEncoder = null;
        var dataMasker = defaultDataMasker;
        if (!(kvp.value instanceof String strMasker) || strMasker.isBlank()) {
            return dataMasker;
        }
        if (strMasker.equalsIgnoreCase(MaskerType.JSON.name())) {
            dataMasker = jsonMasker;
            messageEncoder = jsonEncoder;
        } else if (strMasker.equalsIgnoreCase(MaskerType.XML.name())) {
            dataMasker = xmlMasker;
            messageEncoder = xmlEncoder;
        }
        if (defaultDataMasker instanceof NOPMasker nopMasker) {
            if (Objects.nonNull(messageEncoder)) {
                nopMasker.setMessageEncoder(messageEncoder);
            }
            return nopMasker;
        }
        return dataMasker;
    }

    private MessageLifecycle getMessageLifecycle(KeyValuePair kvp) {
        if (kvp.value instanceof MessageLifecycle messageLifecycle) {
            return messageLifecycle;
        }
        if (kvp.value instanceof String messageLifecycleStr) {
            try {
                return MessageLifecycle.valueOf(messageLifecycleStr.toUpperCase());
            } catch (Exception e) {
                /* do nothing. will default to MessageLifecycle.ACTION*/
            }
        }
        return MessageLifecycle.ACTION;
    }

    private String getStatus(KeyValuePair kvp, ILoggingEvent event) {
        var status = getString(kvp);
        if (StringUtils.isBlank(status)) {
            status = event.getLevel().equals(Level.ERROR) ? LoggingConstants.STATUS_ERROR : LoggingConstants.STATUS_SUCCESS;
        }
        return status;
    }

    private String getString(KeyValuePair kvp) {
        if (kvp.value instanceof String strValue) {
            return strValue;
        }
        return null;
    }
}
