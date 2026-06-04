package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void serializeItemRequestCreateDto() throws Exception {

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Need a drill\"");
    }

    @Test
    void deserializeItemRequestCreateDto() throws Exception {

        String json = """
                {
                  "description":"Need a drill"
                }
                """;

        ItemRequestCreateDto dto =
                mapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }
}