package io.github.stepio.lambda.util;

import org.junit.Test;

import static io.github.stepio.lambda.util.StringUtils.hasLength;
import static io.github.stepio.lambda.util.StringUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @Test
    public void hasLengthWithEmpty() {
        assertThat(hasLength(null)).isFalse();
        assertThat(hasLength("")).isFalse();
    }

    @Test
    public void hasLengthWithNotEmpty() {
        assertThat(hasLength(" ")).isTrue();
        assertThat(hasLength("qwerty")).isTrue();
        assertThat(hasLength("123")).isTrue();
    }

    @Test
    public void isEmptyWithEmpty() {
        assertThat(isEmpty(null)).isTrue();
        assertThat(isEmpty("")).isTrue();
    }

    @Test
    public void isEmptyWithNotEmpty() {
        assertThat(isEmpty(" ")).isFalse();
        assertThat(isEmpty("qwerty")).isFalse();
        assertThat(isEmpty("123")).isFalse();
    }
}
