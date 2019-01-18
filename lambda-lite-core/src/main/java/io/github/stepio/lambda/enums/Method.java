package io.github.stepio.lambda.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Method {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String value;

    Method(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public boolean matches(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public static Optional<Method> of(String value) {
        return Arrays.stream(values())
                .filter(status -> status.matches(value))
                .findAny();
    }
}
