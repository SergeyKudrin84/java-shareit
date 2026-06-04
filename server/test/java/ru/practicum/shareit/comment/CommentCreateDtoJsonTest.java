package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoJsonTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Test
    void serializeCommentCreateDto() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Good item");

        var result = json.write(dto);

        assertThat(result)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo("Good item");
    }

    @Test
    void deserializeCommentCreateDto() throws Exception {

        String content = """
                {
                    "text":"Good item"
                }
                """;

        CommentCreateDto dto = json.parseObject(content);

        assertThat(dto.getText())
                .isEqualTo("Good item");
    }
}