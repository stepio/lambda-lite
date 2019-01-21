package io.github.stepio.lambda.util;

/**
 * Implements basic validations, similar to {@code org.springframework.util.StringUtils}.
 */
public class StringUtils {

    private StringUtils() {
    }

    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }
}
