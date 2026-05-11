package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addNewItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    Item getItem(Long itemId);

    List<Item> searchItem(String text);

    List<Item> getAllByOwner(Long userId);
}
