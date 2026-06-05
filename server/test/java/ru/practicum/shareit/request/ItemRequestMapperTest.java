package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toRequestDto_shouldMap() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need drill");
        request.setCreated(LocalDateTime.now());

        ItemRequestDto dto = mapper.toRequestDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void toItemForRequestDto_shouldMap() {
        User owner = new User();
        owner.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setOwner(owner);

        ItemForRequestDto dto = mapper.toItemForRequestDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals(10L, dto.getOwnerId());
    }

    @Test
    void toRequestDtoList_shouldMapList() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need drill");

        List<ItemRequestDto> result = mapper.toRequestDtoList(List.of(request));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void toItemForRequestDtoList_shouldMapList() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("Need drill");

        List<ItemForRequestDto> result = mapper.toItemForRequestDtoList(List.of(item));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
