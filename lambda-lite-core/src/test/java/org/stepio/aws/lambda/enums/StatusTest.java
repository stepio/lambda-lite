package org.stepio.aws.lambda.enums;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class StatusTest {

    @Test
    public void of_values() {
        Optional<Status> optional = Status.of(42);
        assertThat(optional.isPresent()).isFalse();

        for (Status status : Status.values()) {
            optional = Status.of(status.getStatusCode());
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isSameAs(status);
        }
    }
}
