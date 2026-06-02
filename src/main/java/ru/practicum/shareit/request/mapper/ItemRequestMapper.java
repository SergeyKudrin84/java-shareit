package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {
    ItemRequestDto toRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toRequestDtoList(List<ItemRequest> itemRequests);

    ItemForRequestDto toItemForRequestDto(Item item);

    List<ItemForRequestDto> toItemForRequestDtoList(List<Item> items);
}
