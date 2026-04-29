package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final List<Item> items = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final Validator validator;

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        log.info("Creating new item userId = {}, itemDto =  {}", userId, itemDto);
        User user = userRepository.getUserById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(idGenerator.getAndIncrement());
        item.setOwner(user);
        validate(item);
        items.add(item);
        log.info("New item created item = {}", item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Updating item userId = {}, itemId = {},  itemDto = {}", userId, itemId, itemDto);
        userRepository.getUserById(userId);
        Item item = getItem(itemId);
        log.debug("item before update = {}", item);
        if (!item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может менять предмет");
        }
        Item updatedItem = itemMapper.toItem(itemDto);

        if (updatedItem.getName() == null || updatedItem.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (updatedItem.getDescription() == null || updatedItem.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        validate(updatedItem);
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setAvailable(updatedItem.getAvailable());
        log.info("item after update = {}", item);
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        log.info("Getting item with id = {}", itemId);
        return items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
    }

    @Override
    public List<Item> searchItem(String text) {
        log.info("Searching items by text {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lower = text.toLowerCase();
        List<Item> foundedItems = items.stream()
                .filter(Item::getAvailable)
                .filter(i ->
                        i.getName().toLowerCase().contains(lower) ||
                                i.getDescription().toLowerCase().contains(lower)
                )
                .collect(Collectors.toList());
        log.info("Founded items by text {}", foundedItems.size());
        return foundedItems;
    }

    @Override
    public List<Item> getAllByOwner(Long userId) {
        log.info("Getting all items by owner {}", userId);
        userRepository.getUserById(userId);
        List<Item> foundedItems = items.stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        log.info("Founded items by owner {}", foundedItems.size());
        return foundedItems;
    }

    public void validate(Item item) throws RuntimeException {

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            log.warn(message);
            throw new RuntimeException(message);
        }
    }
}
