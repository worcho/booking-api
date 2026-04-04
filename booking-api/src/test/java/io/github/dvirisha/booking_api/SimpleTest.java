package io.github.dvirisha.booking_api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimpleTest {

    @Test
    void shouldReturnTrue(){
        int result = 3 + 2;

        assertThat(result).isEqualTo(5);
    }

    @Test
    void shouldThrowException() {
        assertThatThrownBy(() -> {
            throw new RuntimeException("error");
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("error");
    }
}
