package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Test
    void create_shouldReturnOk() throws Exception {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.create(anyLong(), any()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient)
                .create(eq(1L), any(BookItemRequestDto.class));
    }

    @Test
    void approve_shouldReturnOk() throws Exception {

        when(bookingClient.approve(1L, 1L, true))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(
                        patch("/bookings/1")
                                .header("X-Sharer-User-Id", 1)
                                .param("approved", "true")
                )
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {

        when(bookingClient.getById(1L, 1L))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_shouldReturnOk() throws Exception {

        when(bookingClient.getUserBookings(
                1L,
                BookingState.ALL))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_shouldReturnOk() throws Exception {

        when(bookingClient.getOwnerBookings(
                1L,
                BookingState.ALL))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnBadRequestWhenStartInPast() throws Exception {

        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}
