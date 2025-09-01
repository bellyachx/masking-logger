package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonPointer;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

final class MaskingPathConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 108774835281027390L;

    private final Map<JsonPointer, Boolean> cachedPointers = new HashMap<>();

    private final transient PropertyProvider propertyProvider;

    private Set<JsonPointer> exactPointers;
    private List<Pattern> wildcardPatterns;

    public MaskingPathConfig(PropertyProvider propertyProvider) {
        this.propertyProvider = propertyProvider;
    }

    public static MaskingPathConfig from(PropertyProvider propertyProvider) {
        return new MaskingPathConfig(propertyProvider);
    }

    private static String normalize(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException("Empty mask path");
        }
        var p = s.trim();
        if (!p.startsWith("/")) p = "/" + p;
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    private static Pattern compileWildcard(String wildcardPath) {
        var escaped = escapeRegex(wildcardPath);
        var regex = escaped.replace("/\\#", "/\\d+");
        return Pattern.compile("^%s$".formatted(regex));
    }

    private static String escapeRegex(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ("[](){}.*+?$^|#\\".indexOf(c) >= 0) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean matches(JsonPointer jsonPointer) {
        if (!init()) {
            return false;
        }
        if (cachedPointers.containsKey(jsonPointer)) {
            return cachedPointers.get(jsonPointer);
        }
        var asString = jsonPointer.toString();
        if (exactPointers.contains(jsonPointer)) {
            cachedPointers.put(jsonPointer, true);
            return true;
        }
        for (var pattern : wildcardPatterns) {
            if (pattern.matcher(asString).matches()) {
                cachedPointers.put(jsonPointer, true);
                return true;
            }
        }
        cachedPointers.put(jsonPointer, false);
        return false;
    }

    private boolean init() {
        if (exactPointers != null && wildcardPatterns != null) {
            return true;
        }

        if (propertyProvider.getLoggingProps() != null) {
            var paths = propertyProvider.getLoggingProps().getFields();
            var exact = new HashSet<JsonPointer>();
            var wildcards = new ArrayList<Pattern>();

            for (var path : paths) {
                var normalized = normalize(path);
                if (normalized.contains("#")) {
                    wildcards.add(compileWildcard(normalized));
                } else {
                    exact.add(JsonPointer.compile(normalized));
                }
            }
            exactPointers = exact;
            wildcardPatterns = wildcards;
            return true;
        }

        return false;
    }

}
