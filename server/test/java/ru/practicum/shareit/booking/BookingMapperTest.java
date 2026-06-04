package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toBookingDto_shouldMap() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto = mapper.toBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    void toBooking_shouldMapCreateDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = mapper.toBooking(dto);

        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingDtoList_shouldMapList() {
        Booking booking = new Booking();
        booking.setId(1L);

        List<BookingDto> result = mapper.toBookingDtoList(List.of(booking));

        assertEquals(1, result.size());
    }
}
