package io.github.stepio.lambda.util;

import static java.lang.String.format;

/**
 * Implements basic validations, similar to {@link org.springframework.util.Assert}.
 */
public class Assert {

    private Assert() {
    }

    public static void notNull(Object value, String comment, Object... params) {
        if (value == null) {
            throw new IllegalArgumentException(message(comment, params));
        }
    }

    public static void hasLength(String value, String comment, Object... params) {
        if (!StringUtils.hasLength(value)) {
            throw new IllegalArgumentException(message(comment, params));
        }
    }

    public static void state(boolean flag, String comment, Object... params) {
        if (!flag) {
            throw new IllegalStateException(message(comment, params));
        }
    }

    public static String message(String message, Object... params) {
        String result = message;
        if (StringUtils.hasLength(message)) {
            result = format(message, params);
        }
        return result;
    }
}
