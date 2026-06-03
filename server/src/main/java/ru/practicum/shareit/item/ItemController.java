package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader(userHeader) Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping({"/{itemId}"})
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestHeader(userHeader) Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping({"/{itemId}"})
    public ResponseEntity<ItemWithBookingDto> getItem(@PathVariable Long itemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingDto>> getAllByOwner(@RequestHeader(userHeader) Long userId) {
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

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(userHeader) Long userId,
                                                 @PathVariable Long itemId,
                                                 @Valid @RequestBody CommentCreateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.addComment(userId, itemId, dto));
    }
}
