package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Maintains a per-serialization stack of sets of absolute JsonPointer paths that should be masked,
 * derived from relative paths declared on enclosing complex properties.
 * <p>
 * Uses SerializerProvider attributes so the scope is bound to the current serialization call.
 */
public final class RelativePathMaskingScope implements AutoCloseable {

    private static final Object ATTR_KEY = RelativePathMaskingScope.class;

    private final SerializerProvider provider;
    private final List<PathPattern> pushedPatterns;

    private RelativePathMaskingScope(SerializerProvider provider, List<PathPattern> pushedPatterns) {
        this.provider = provider;
        this.pushedPatterns = pushedPatterns;
    }

    static RelativePathMaskingScope push(SerializerProvider provider,
                                         JsonPointer base, String[] relativePaths,
                                         boolean autoIndexAfterBase) {
        @SuppressWarnings("unchecked")
        var stack = (Deque<List<PathPattern>>) provider.getAttribute(ATTR_KEY);
        if (stack == null) {
            stack = new ArrayDeque<>();
            provider.setAttribute(ATTR_KEY, stack);
        }

        var baseSegs = toSegments(base);
        var compiled = new ArrayList<PathPattern>();
        if (relativePaths != null) {
            compilePatterns(relativePaths, compiled, baseSegs, autoIndexAfterBase);
        }

        stack.push(compiled);
        return new RelativePathMaskingScope(provider, compiled);
    }

    private static void compilePatterns(String[] relativePaths, ArrayList<PathPattern> compiled,
                                        List<Seg> baseSegs, boolean autoIndexAfterBase) {
        Arrays.stream(relativePaths)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .forEach(rel -> compiled.add(compilePattern(baseSegs, rel, autoIndexAfterBase)));
    }


    static boolean matches(SerializerProvider provider, JsonPointer current) {
        @SuppressWarnings("unchecked")
        var stack = (Deque<List<PathPattern>>) provider.getAttribute(ATTR_KEY);
        if (stack == null || stack.isEmpty()) return false;

        var currentSegs = toSegments(current);
        for (var frame : stack) {
            for (var pattern : frame) {
                if (pattern.matches(currentSegs)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void close() {
        @SuppressWarnings("unchecked")
        var stack = (Deque<List<PathPattern>>) provider.getAttribute(ATTR_KEY);
        if (stack == null || stack.isEmpty()) return;
        if (stack.peek() == pushedPatterns) {
            stack.pop();
        } else {
            stack.remove(pushedPatterns);
        }
        if (stack.isEmpty()) {
            provider.setAttribute(ATTR_KEY, null);
        }
    }

    private static PathPattern compilePattern(List<Seg> base,
                                              String relativePath,
                                              boolean autoIndexAfterBase) {
        var rel = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        var tokens = rel.isEmpty() ? new String[0] : rel.split("/");
        var segs = new ArrayList<Seg>(base.size() + tokens.length);
        segs.addAll(base);

        var optionalIndexPos = ensureIndexPresent(base, segs, tokens, autoIndexAfterBase);

        for (var t : tokens) {
            if (t.equals("#")) {
                segs.add(Seg.indexAny());
            } else if (StringUtils.isNumeric(t)) {
                segs.add(Seg.index(Integer.parseInt(t)));
            } else {
                segs.add(Seg.property(t));
            }
        }
        return new PathPattern(segs, optionalIndexPos);
    }

    private static int ensureIndexPresent(List<Seg> base, List<Seg> segs, String[] tokens,
                                          boolean autoIndexAfterBase) {
        // Avoid double index segments:
        // add INDEX_ANY only if we are inside an array, base does NOT already end with an index,
        // and the first user token is NOT an explicit index ('#' or numeric).
        var optionalIndexPos = -1;
        if (autoIndexAfterBase) {
            var baseEndsWithIndex = !base.isEmpty() && base.getLast().kind == Seg.Kind.INDEX;
            if (!baseEndsWithIndex) {
                var startsWithExplicitIndex = tokens.length > 0 && ("#".equals(tokens[0]) || StringUtils.isNumeric(tokens[0]));
                if (!startsWithExplicitIndex) {
                    optionalIndexPos = segs.size();
                    segs.add(Seg.indexAny());
                }
            } else {
                // replace last segment with INDEX_ANY
                segs.set(segs.size() - 1, Seg.indexAny());
            }
        }
        return optionalIndexPos;
    }

    private static List<Seg> toSegments(JsonPointer ptr) {
        var list = new ArrayList<Seg>();
        var cursor = ptr;
        while (cursor != null && !cursor.matches()) {
            if (cursor.mayMatchElement()) {
                list.add(Seg.index(cursor.getMatchingIndex()));
            } else if (cursor.mayMatchProperty()) {
                list.add(Seg.property(cursor.getMatchingProperty()));
            }
            cursor = cursor.tail();
        }
        return list;
    }

    private static final class PathPattern {
        private final List<Seg> segments;
        private final int optionalIndexPos;

        private PathPattern(List<Seg> segments, int optionalIndexPos) {
            this.segments = segments;
            this.optionalIndexPos = optionalIndexPos;
        }

        boolean matches(List<Seg> current) {
            // Fast path: exact size match
            if (current.size() == segments.size()) {
                for (int i = 0; i < segments.size(); i++) {
                    var p = segments.get(i);
                    var c = current.get(i);
                    if (!p.matches(c)) {
                        return false;
                    }
                }
                return true;
            }

            // Optional-index path: allow current to be missing exactly the auto-inserted index segment
            if (optionalIndexPos >= 0 && current.size() + 1 == segments.size()) {
                int ci = 0;
                for (int pi = 0; pi < segments.size(); pi++) {
                    if (pi == optionalIndexPos) {
                        // Skip the optional INDEX_ANY segment in the pattern
                        continue;
                    }
                    if (ci >= current.size()) return false;
                    var p = segments.get(pi);
                    var c = current.get(ci);
                    if (!p.matches(c)) {
                        return false;
                    }
                    ci++;
                }
                return true;
            }

            return false;
        }
    }

    private static final class Seg {
        enum Kind {PROPERTY, INDEX, INDEX_ANY}

        final Kind kind;
        final String prop;   // when PROPERTY
        final Integer index; // when INDEX

        private Seg(Kind kind, String prop, Integer index) {
            this.kind = kind;
            this.prop = prop;
            this.index = index;
        }

        static Seg property(String name) {
            return new Seg(Kind.PROPERTY, name, null);
        }

        static Seg index(int idx) {
            return new Seg(Kind.INDEX, null, idx);
        }

        static Seg indexAny() {
            return new Seg(Kind.INDEX_ANY, null, null);
        }

        boolean matches(Seg current) {
            if (this.kind == Kind.PROPERTY) {
                return current.kind == Kind.PROPERTY && Objects.equals(this.prop, current.prop);
            }
            if (this.kind == Kind.INDEX) {
                return current.kind == Kind.INDEX && Objects.equals(this.index, current.index);
            }
            // INDEX_ANY: match any array index
            return current.kind == Kind.INDEX;
        }
    }

}

