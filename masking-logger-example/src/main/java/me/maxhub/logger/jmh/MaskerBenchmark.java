package me.maxhub.logger.jmh;

import ch.qos.logback.classic.LoggerContext;
import me.maxhub.logger.api.WLogger;
import me.maxhub.logger.jmh.props.JmhPropertyProvider;
import me.maxhub.logger.jmh.util.LogbackBenchUtil;
import me.maxhub.logger.jmh.util.TestDataProvider;
import me.maxhub.logger.logback.encoder.MaskingJsonEncoder;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.mask.enums.MaskerVersion;
import org.openjdk.jmh.annotations.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(jvmArgsAppend = {"-Dlogback.configurationFile=unused.xml"})
public class MaskerBenchmark {

    MaskingJsonEncoder encoder;
    LoggerContext ctx;
    org.slf4j.Logger log;

    @Param({"V1", "V2"})
    public String maskerVersion;

    @Setup(Level.Trial)
    public void setup() {
        var fieldsToMask = Set.of(
            "/stringArray/#",
            "/string",
            "/testData/integer",
            "/testDataList/#/bigDecimal",
            "/testDataList/#/testDataList/#/integer",
            "/stringList/#/"
        );
        var jmhPropertyProvider = new JmhPropertyProvider(MaskerVersion.valueOf(maskerVersion), MaskerType.JSON, fieldsToMask);
        encoder = new MaskingJsonEncoder();
        encoder.setPropertyProvider(jmhPropertyProvider);
        ctx = LogbackBenchUtil.reconfigureWithEncoder(encoder, null);
        log = org.slf4j.LoggerFactory.getLogger("bench");
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        ctx.stop();
    }

    @Benchmark
    public void init() {
        WLogger.info()
            .message("Hello WLogger")
            .messageBody(TestDataProvider.createTestData())
            .operationName("test")
            .status("test")
            .log();
    }
}
