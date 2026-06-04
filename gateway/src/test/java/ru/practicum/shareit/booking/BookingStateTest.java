package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_shouldReturnAll() {
        Optional<BookingState> result = BookingState.from("ALL");

        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_shouldIgnoreCase() {
        Optional<BookingState> result = BookingState.from("future");

        assertTrue(result.isPresent());
        assertEquals(BookingState.FUTURE, result.get());
    }

    @Test
    void from_shouldReturnEmptyForUnknownState() {
        Optional<BookingState> result = BookingState.from("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void from_shouldReturnEmptyForEmptyString() {
        Optional<BookingState> result = BookingState.from("");

        assertTrue(result.isEmpty());
    }
}
