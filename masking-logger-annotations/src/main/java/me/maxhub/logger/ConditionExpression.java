package me.maxhub.logger;

import java.util.Collection;
import java.util.Objects;

public enum ConditionExpression {
    EQUALS() {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            return Objects.equals(value, condition.expected()) ^ condition.negate();
        }
    },
    GREATER_THAN {
        @Override
        @SuppressWarnings("unchecked")
        public boolean evaluate(Object value, Condition condition) {
            return (isComparable(value, condition.expected()) && ((Comparable) value).compareTo(condition.expected()) > 0) ^ condition.negate();
        }
    },
    LESS_THAN {
        @Override
        @SuppressWarnings("unchecked")
        public boolean evaluate(Object value, Condition condition) {
            return (isComparable(value, condition.expected()) && ((Comparable) value).compareTo(condition.expected()) < 0) ^ condition.negate();
        }
    },
    GREATER_THAN_OR_EQUALS {
        @Override
        @SuppressWarnings("unchecked")
        public boolean evaluate(Object value, Condition condition) {
            return (isComparable(value, condition.expected()) && ((Comparable) value).compareTo(condition.expected()) >= 0) ^ condition.negate();
        }
    },
    LESS_THAN_OR_EQUALS {
        @Override
        @SuppressWarnings("unchecked")
        public boolean evaluate(Object value, Condition condition) {
            return (isComparable(value, condition.expected()) && ((Comparable) value).compareTo(condition.expected()) <= 0) ^ condition.negate();
        }
    },
    CONTAINS {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.contains(strPattern) ^ condition.negate();
            } else if (value instanceof Collection<?> collection) {
                return collection.contains(condition.expected()) ^ condition.negate();
            }
            return condition.negate();
        }
    },
    STARTS_WITH {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.startsWith(strPattern) ^ condition.negate();
            }
            return condition.negate();
        }
    },
    ENDS_WITH {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            if (value instanceof String strValue && condition.expected() instanceof String strPattern) {
                return strValue.endsWith(strPattern) ^ condition.negate();
            }
            return condition.negate();
        }
    },
    IS_NULL {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            return Objects.isNull(value) ^ condition.negate();
        }
    },
    IS_EMPTY {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            if (value instanceof String string) {
                return string.isEmpty() ^ condition.negate();
            } else if (value instanceof Collection<?> collection) {
                return collection.isEmpty() ^ condition.negate();
            }

            return condition.negate();
        }
    },
    IS_INSTANCE_OF {
        @Override
        public boolean evaluate(Object value, Condition condition) {
            return condition.expectedType().isInstance(value) ^ condition.negate();
        }
    };

    public abstract boolean evaluate(Object value, Condition condition);

    private static boolean isComparable(Object value, Object expected) {
        return value instanceof Comparable && expected instanceof Comparable;
    }
}
