package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;
	private final String userHeader = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(userHeader) Long userId,
											 @Valid @RequestBody BookItemRequestDto bookItemRequestDto) {
		log.info("Creating booking {}, userId={}", bookItemRequestDto, userId);
		return bookingClient.create(userId, bookItemRequestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader(userHeader) Long ownerId,
											  @PathVariable Long bookingId,
											  @RequestParam(required = true) boolean approved) {
		log.info("Approve booking {} by ownerId {}", bookingId, ownerId);
		return bookingClient.approve(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader(userHeader) Long userId,
											  @PathVariable Long bookingId) {
		log.info("Get booking by id {}", bookingId);
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(userHeader) Long userId,
												  @RequestParam(defaultValue = "ALL") BookingState state) {
		log.info("Get bookings by userId {}", userId);
		return bookingClient.getUserBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(userHeader) Long ownerId,
															 @RequestParam(defaultValue = "ALL") BookingState state) {
		log.info("Get owner bookings by ownerId {}", ownerId);
		return bookingClient.getOwnerBookings(ownerId, state);
	}

	/*private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}*/
}
