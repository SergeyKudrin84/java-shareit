package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void serializeDto() throws Exception {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 2, 10, 0)
        );

        var result = json.write(dto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);

        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-01-01T10:00:00");

        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-01-02T10:00:00");
    }

    @Test
    void deserializeDto() throws Exception {

        String content = "{\"itemId\":1,\"start\":\"2026-01-01T10:00:00\",\"end\":\"2026-01-02T10:00:00\"} ";

        BookItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart())
                .isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));

        assertThat(dto.getEnd())
                .isEqualTo(LocalDateTime.of(2026, 1, 2, 10, 0));
    }
}