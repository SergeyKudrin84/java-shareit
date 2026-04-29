package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping({"/{itemId}"})
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestHeader(USER_HEADER) Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping({"/{itemId}"})
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByOwner(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getAllByOwner(userId));
    }

    @GetMapping({"/search"})
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam(required = true) String text) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.searchItem(text));
    }
}
