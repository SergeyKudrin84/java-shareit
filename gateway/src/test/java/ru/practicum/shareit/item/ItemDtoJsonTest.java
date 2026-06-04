package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void serializeItemDto() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        dto.setRequestId(10L);

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"description\":\"Power drill\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":10");
    }

    @Test
    void deserializeItemDto() throws Exception {

        String json = """
                {
                    "id":1,
                    "name":"Drill",
                    "description":"Power drill",
                    "available":true,
                    "requestId":10
                }
                """;

        ItemDto dto = mapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Power drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(10L);
    }
}