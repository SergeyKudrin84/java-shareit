package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final String userHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(
            @RequestHeader(userHeader) Long userId,
            @Valid @RequestBody ItemRequestCreateDto dto) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getOwnRequests(
            @RequestHeader(userHeader) Long userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getOwnRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(userHeader) Long userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getAllRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequest(
            @RequestHeader(userHeader) Long userId,
            @PathVariable Long requestId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getRequestById(userId, requestId));
    }
}
