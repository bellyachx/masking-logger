package me.maxhub.logger;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;

public enum ConditionExpression {
    EQUALS {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return StringUtils.equals(String.valueOf(value), condition.expected());
        }
    },
    EQUALS_IGNORE_CASE {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return StringUtils.equalsIgnoreCase(String.valueOf(value), condition.expected());
        }
    },
    MATCHES {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            if (value instanceof String strValue) {
                return strValue.matches(condition.expected());
            }
            return false;
        }
    },
    GREATER_THAN {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return compare(value, condition.expected(), res -> res > 0);
        }
    },
    LESS_THAN {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return compare(value, condition.expected(), res -> res < 0);
        }
    },
    GREATER_THAN_OR_EQUALS {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return compare(value, condition.expected(), res -> res >= 0);
        }
    },
    LESS_THAN_OR_EQUALS {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return compare(value, condition.expected(), res -> res <= 0);
        }
    },
    CONTAINS {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.contains(strPattern);
            } else if (value instanceof Collection<?> collection) {
                return collection.contains(condition.expected());
            } else if (value instanceof Map<?, ?> map) {
                return map.containsKey(condition.expected());
            }
            return false;
        }
    },
    STARTS_WITH {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.startsWith(strPattern);
            }
            return false;
        }
    },
    ENDS_WITH {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.endsWith(strPattern);
            }
            return false;
        }
    },
    IS_NULL {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return Objects.isNull(value);
        }
    },
    IS_EMPTY {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            if (Objects.isNull(value)) {
                return true; // null -> is empty
            }
            return switch (value) {
                case String string -> string.isEmpty();
                case Collection<?> collection -> collection.isEmpty();
                case Object[] array -> array.length == 0;
                case Map<?, ?> map -> map.isEmpty();
                default -> true;
            };
        }
    },
    IS_INSTANCE_OF {
        @Override
        public boolean evaluateInternal(Object value, Condition condition) {
            return (condition.expectedType() != null && condition.expectedType().isInstance(value));
        }
    };

    public boolean evaluate(Object value, Condition condition) {
        return evaluateInternal(value, condition) ^ condition.negate();
    }

    protected abstract boolean evaluateInternal(Object value, Condition condition);

    boolean compare(Object value, String expected, IntPredicate comparator) {
        if (!(value instanceof Comparable)) {
            return false;
        }
        return switch (value) {
            case Integer intValue -> comparator.test(intValue.compareTo(Integer.parseInt(expected)));
            case Long longValue -> comparator.test(longValue.compareTo(Long.parseLong(expected)));
            case Double doubleValue -> comparator.test(doubleValue.compareTo(Double.parseDouble(expected)));
            case Float floatValue -> comparator.test(floatValue.compareTo(Float.parseFloat(expected)));
            case Short shortValue -> comparator.test(shortValue.compareTo(Short.parseShort(expected)));
            case Byte byteValue -> comparator.test(byteValue.compareTo(Byte.parseByte(expected)));
            case BigDecimal bigDecimalValue -> comparator.test(bigDecimalValue.compareTo(new BigDecimal(expected)));
            case BigInteger bigIntegerValue -> comparator.test(bigIntegerValue.compareTo(new BigInteger(expected)));
            default -> comparator.test(String.valueOf(value).compareTo(expected));
        };
    }
}
