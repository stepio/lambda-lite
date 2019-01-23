package io.github.stepio.lambda.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaTypeTest {

    @Test
    public void getValueExpectStandardMediaType() {
        assertThat(MediaType.ALL.getValue()).isEqualTo("*/*");
        assertThat(MediaType.APPLICATION_JSON.getValue()).isEqualTo("application/json");
        assertThat(MediaType.TEXT_HTML.getValue()).isEqualTo("text/html");
        assertThat(MediaType.TEXT_PLAIN.getValue()).isEqualTo("text/plain");
    }
}
