package me.maxhub.logger.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.maxhub.logger.LoggingContext;
import me.maxhub.logger.util.LoggingConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoggingContextRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put(LoggingConstants.RQ_UID, request.getHeader("X-Rq-Uid"));
            MDC.put(LoggingConstants.OP_NAME, request.getHeader("X-Rq-OpName"));
            MDC.put(LoggingConstants.RQIP, getClientIpAddress(request));
            MDC.put(LoggingConstants.CLIENT_ID, getAttributeFromSession(request, LoggingConstants.CLIENT_ID));
            LoggingContext.put(LoggingConstants.HEADERS, buildHeaders(request));
            filterChain.doFilter(request, response);
        } finally {
            LoggingContext.clear();
            MDC.remove(LoggingConstants.RQ_UID);
            MDC.remove(LoggingConstants.OP_NAME);
            MDC.remove(LoggingConstants.RQIP);
            MDC.remove(LoggingConstants.CLIENT_ID);
        }
    }

    private Map<String, String> buildHeaders(HttpServletRequest request) {
        var headerNames = request.getHeaderNames();
        if (Objects.isNull(headerNames) || !headerNames.hasMoreElements()) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            var header = headerNames.nextElement();
            if (Objects.nonNull(header)) {
                headers.put(header, request.getHeader(header));
            }
        }
        return headers;
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        try {
            var ipAddress = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress)) {
                return ipAddress.split(",")[0].trim();
            }
            var remoteAddr = request.getRemoteAddr();
            return StringUtils.isNotBlank(remoteAddr) ? remoteAddr : StringUtils.EMPTY;
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static String getAttributeFromSession(HttpServletRequest request, String key) {
        try {
            HttpSession session = request.getSession(false);
            if (Objects.isNull(session)) {
                return StringUtils.EMPTY;
            }
            var value = session.getAttribute(key);
            return Objects.nonNull(value) ? value.toString() : StringUtils.EMPTY;
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }
}
