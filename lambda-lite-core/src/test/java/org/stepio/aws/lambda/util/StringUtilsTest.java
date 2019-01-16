package org.stepio.aws.lambda.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stepio.aws.lambda.util.StringUtils.hasLength;
import static org.stepio.aws.lambda.util.StringUtils.isEmpty;

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

    @Test
    public void isEmpty_isEmpty() {
        assertThat(isEmpty(null)).isTrue();
        assertThat(isEmpty("")).isTrue();
    }

    @Test
    public void isEmpty_isNotEmpty() {
        assertThat(isEmpty(" ")).isFalse();
        assertThat(isEmpty("qwerty")).isFalse();
        assertThat(isEmpty("123")).isFalse();
    }
}
