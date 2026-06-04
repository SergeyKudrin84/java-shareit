package ru.practicum.shareit.booking;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookItemRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void shouldPassValidation() {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThat(validator.validate(dto))
                .isEmpty();
    }

    @Test
    void shouldFailWhenStartInPast() {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(validator.validate(dto))
                .hasSize(1);
    }

    @Test
    void shouldFailWhenEndInPast() {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        assertThat(validator.validate(dto))
                .hasSize(1);
    }
}