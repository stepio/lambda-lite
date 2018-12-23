package org.stepio.aws.lambda.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stepio.aws.lambda.util.StringUtils.hasLength;

public class StringUtilsTest {

    @Test
    public void hasLength_isEmpty() {
        assertThat(hasLength(null)).isFalse();
        assertThat(hasLength("")).isFalse();
    }

    @Test
    public void hasLength_isNotEmpty() {
        assertThat(hasLength(" ")).isTrue();
        assertThat(hasLength("qwerty")).isTrue();
        assertThat(hasLength("123")).isTrue();
    }
}
