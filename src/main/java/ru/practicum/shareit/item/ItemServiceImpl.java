package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        return itemMapper.toItemDto(itemRepository.addNewItem(userId, itemDto));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemMapper.toItemDto(itemRepository.updateItem(userId, itemId, itemDto));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemMapper.toItemDtoList(itemRepository.searchItem(text));
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        return itemMapper.toItemDtoList(itemRepository.getAllByOwner(userId));
    }
}
