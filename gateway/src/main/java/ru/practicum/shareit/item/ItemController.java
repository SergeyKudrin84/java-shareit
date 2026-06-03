package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader(userHeader) Long userId,
                                              @RequestBody @Valid ItemDto itemDto) {
        log.info("Add new item {} by userid {}", itemDto, userId);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PatchMapping({"/{itemId}"})
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                              @RequestHeader(userHeader) Long userId,
                                              @RequestBody ItemDto itemDto) {
        log.info("Update item {} by userid {}", itemDto, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping({"/{itemId}"})
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Get item {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(userHeader) Long userId) {
        log.info("Get all items by ownerId {}", userId);
        return itemClient.getAllByOwner(userId);
    }

    @GetMapping({"/search"})
    public ResponseEntity<Object> searchItem(@RequestHeader(userHeader) Long userId,
                                                   @RequestParam(required = true) String text) {
        log.info("Search items by ownerId {}", userId, text);
        return itemClient.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(userHeader) Long userId,
                                                 @PathVariable Long itemId,
                                                 @Valid @RequestBody CommentCreateDto dto) {
        log.info("Add comment {} by userid {}", dto, userId);
        return itemClient.addComment(userId, itemId, dto);
    }
}
