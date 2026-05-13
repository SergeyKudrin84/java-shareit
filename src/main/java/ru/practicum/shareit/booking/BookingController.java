package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader(userHeader) Long userId,
                                             @Valid @RequestBody BookingCreateDto bookingCreatedto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.create(userId, bookingCreatedto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader(userHeader) Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam(required = true) boolean approved) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.approve(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader(userHeader) Long userId,
                                              @PathVariable Long bookingId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(userHeader) Long userId,
                                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getUserBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader(userHeader) Long ownerId,
                                                             @RequestParam(defaultValue = "ALL") BookingState state) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getOwnerBookings(ownerId, state));
    }
}
