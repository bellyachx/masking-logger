package me.maxhub.logger.aop;

import me.maxhub.logger.LoggingContext;
import me.maxhub.logger.api.WLogger;
import me.maxhub.logger.util.LoggingConstants;
import me.maxhub.logger.util.MessageLifecycle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
public class RequestMappingInterceptor {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = getClass();
        try {
            var signature = (MethodSignature) joinPoint.getSignature();
            var method = signature.getMethod();
            var args = joinPoint.getArgs();
            clazz = method.getDeclaringClass();

            var methodContext = buildMethodContext(method, args);

            var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            var requestUri = request.getRequestURI();
            var httpMethod = request.getMethod();

            var requestHeaders = methodContext.getRequestHeaders();
            WLogger.with(clazz, Level.INFO)
                .message("Incoming HTTP Request: [{}] - [{}] ", httpMethod, requestUri)
                .headers(requestHeaders)
                .messageBody(new MessageBody(methodContext))
                .messageLifecycle(MessageLifecycle.RECEIVED)
                .log();
        } catch (Exception e) {
            WLogger.error()
                .message("Error occurred when logging request").eventBuilder()
                .setCause(e)
                .log();
        }

        var response = joinPoint.proceed();

        try {
            Map<String, String> headers;
            Object responseBody;
            if (response instanceof ResponseEntity<?> responseEntity) {
                headers = responseEntity.getHeaders().toSingleValueMap();
                responseBody = responseEntity.getBody();
            } else {
                headers = (Map<String, String>) LoggingContext.get(LoggingConstants.HEADERS);
                responseBody = response;
            }
            WLogger.with(clazz, Level.INFO)
                .message("Returning HTTP Response")
                .headers(headers)
                .messageBody(responseBody)
                .messageLifecycle(MessageLifecycle.SENT)
                .log();
        } catch (Exception e) {
            WLogger.error()
                .message("Error occurred when logging response").eventBuilder()
                .setCause(e)
                .log();
        }

        return response;
    }

    private MethodContext buildMethodContext(Method method, Object[] args) {
        var params = method.getParameters();
        var methodContextBuilder = MethodContext.builder();
        Object headers = null;
        Map<String, Object> otherArgs = HashMap.newHashMap(args.length);

        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            if (param.isAnnotationPresent(LogIgnore.class)) {
                continue;
            }
            if (param.isAnnotationPresent(RequestBody.class)) {
                methodContextBuilder.requestBody(args[i]);
            } else if (param.isAnnotationPresent(RequestHeader.class)) {
                headers = args[i];
            } else {
                otherArgs.put(param.getName(), args[i]);
            }
        }

        if (Objects.isNull(headers)) {
            headers = LoggingContext.get(LoggingConstants.HEADERS);
        }
        if (Objects.nonNull(headers)) {
            if (headers instanceof Map<?, ?> mapHeaders) {
                methodContextBuilder.requestHeaders((Map<String, String>) mapHeaders);
            } else if (headers instanceof HttpHeaders httpHeaders) {
                methodContextBuilder.requestHeaders(httpHeaders.toSingleValueMap());
            }
        }

        methodContextBuilder.arguments(otherArgs);

        return methodContextBuilder.build();
    }
}
