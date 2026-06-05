package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoJsonTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void serializeCommentCreateDto() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item");

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Great item\"");
    }

    @Test
    void deserializeCommentCreateDto() throws Exception {

        String json = "{\"text\":\"Great item\"}";

        CommentCreateDto dto = mapper.readValue(json, CommentCreateDto.class);

        assertThat(dto.getText()).isEqualTo("Great item");
    }
}
