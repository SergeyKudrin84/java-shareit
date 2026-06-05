package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serializeCommentDto() throws Exception {

        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Good item");
        dto.setAuthorName("Ivan");
        dto.setCreated(LocalDateTime.of(
                2026,
                1,
                10,
                12,
                30
        ));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Good item");

        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Ivan");

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-01-10T12:30:00");
    }

    @Test
    void deserializeCommentDto() throws Exception {

        String content = """
                {
                    "id": 1,
                    "text": "Good item",
                    "authorName": "Ivan",
                    "created": "2026-01-10T12:30:00"
                }
                """;

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Good item");
        assertThat(dto.getAuthorName()).isEqualTo("Ivan");
        assertThat(dto.getCreated())
                .isEqualTo(LocalDateTime.of(
                        2026,
                        1,
                        10,
                        12,
                        30
                ));
    }
}