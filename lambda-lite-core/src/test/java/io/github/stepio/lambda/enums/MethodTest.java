package io.github.stepio.lambda.enums;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodTest {

    @Test
    public void ofWithAllValues() {
        Optional<Method> optional = Method.of("dummy");
        assertThat(optional.isPresent()).isFalse();

        for (Method method : Method.values()) {
            optional = Method.of(method.toString());
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isSameAs(method);

            optional = Method.of(method.getValue());
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isSameAs(method);

            optional = Method.of(method.getValue().toLowerCase());
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isSameAs(method);
        }
    }
}
