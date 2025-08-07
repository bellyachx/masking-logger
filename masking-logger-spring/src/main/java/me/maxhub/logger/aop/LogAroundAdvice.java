package me.maxhub.logger.aop;

import me.maxhub.logger.api.WLogger;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
public class LogAroundAdvice {

    @Around("@annotation(logAround)")
    public Object around(ProceedingJoinPoint joinPoint, LogAround logAround) throws Throwable {
        Class<?> clazz = getClass();

        Method method = null;
        try {
            var signature = (MethodSignature) joinPoint.getSignature();
            method = signature.getMethod();
            var args = joinPoint.getArgs();
            clazz = method.getDeclaringClass();
            var methodArguments = buildMethodArguments(method, args, logAround);
            WLogger.with(clazz, Level.INFO)
                .message("Method [{}] called", method.getName())
                .messageBody(methodArguments)
                .log();
        } catch (Exception e) {
            WLogger.error()
                .message("Error occurred when logging method parameters").eventBuilder()
                .setCause(e)
                .log();
        }

        var result = joinPoint.proceed();

        if (logAround.logReturn()) {
            try {
                var logMessage = "Method returned";
                if (Objects.nonNull(method)) {
                    logMessage = "Method [%s] returned".formatted(method.getName());
                }
                WLogger.with(clazz, Level.INFO)
                    .message(logMessage)
                    .messageBody(result)
                    .log();
            } catch (Exception e) {
                WLogger.error()
                    .message("Error occurred when logging return value").eventBuilder()
                    .setCause(e)
                    .log();
            }
        }
        return result;
    }

    private MethodArguments buildMethodArguments(Method method, Object[] args, LogAround logAround) {
        var builder = MethodArguments.builder();
        var params = method.getParameters();
        Map<String, Object> argumements = new HashMap<>(args.length);

        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            if (StringUtils.equals(param.getName(), logAround.mask())) {
                builder.requestBody(args[i]);
                continue;
            }
            if (!param.isAnnotationPresent(LogIgnore.class)) {
                argumements.put(param.getName(), args[i]);
            }
        }
        builder.arguments(argumements);
        return builder.build();
    }
}
