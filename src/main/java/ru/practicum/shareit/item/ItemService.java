package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemWithBookingDto getItem(Long itemId);

    List<ItemDto> searchItem(String text);

    List<ItemWithBookingDto> getAllByOwner(Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);

}
