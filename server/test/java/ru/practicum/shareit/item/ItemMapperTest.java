package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toItemDto_shouldMapRequestId() {
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setRequest(request);

        ItemDto dto = mapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals(10L, dto.getRequestId());
        assertNull(dto.getRequest());
    }

    @Test
    void toItem_shouldMapDto() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);

        Item item = mapper.toItem(dto);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItemDtoList_shouldMapList() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Drill");
        item1.setDescription("Drill description");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Hammer");
        item2.setDescription("Hammer description");
        item2.setAvailable(false);

        List<ItemDto> result = mapper.toItemDtoList(List.of(item1, item2));

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Drill", result.get(0).getName());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Hammer", result.get(1).getName());
    }

    @Test
    void toItemWithBookingDto_shouldMapItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);

        ItemWithBookingDto dto = mapper.toItemWithBookingDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals("Good drill", dto.getDescription());
        assertTrue(dto.getAvailable());

        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNull(dto.getComments());
    }
}