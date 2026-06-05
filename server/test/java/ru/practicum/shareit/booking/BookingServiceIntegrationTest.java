package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);
    }

    @Test
    void create_shouldSaveBooking() {

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result =
                bookingService.create(booker.getId(), dto);

        Booking booking =
                bookingRepository.findById(result.getId())
                        .orElseThrow();

        assertEquals(booker.getId(),
                booking.getBooker().getId());

        assertEquals(item.getId(),
                booking.getItem().getId());

        assertEquals(
                BookingStatus.WAITING,
                booking.getStatus()
        );
    }

    @Test
    void approve_shouldChangeStatusToApproved() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        booking = bookingRepository.save(booking);

        bookingService.approve(
                owner.getId(),
                booking.getId(),
                true
        );

        Booking updated =
                bookingRepository.findById(booking.getId())
                        .orElseThrow();

        assertEquals(
                BookingStatus.APPROVED,
                updated.getStatus()
        );
    }

    @Test
    void approve_shouldChangeStatusToRejected() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        booking = bookingRepository.save(booking);

        bookingService.approve(
                owner.getId(),
                booking.getId(),
                false
        );

        Booking updated =
                bookingRepository.findById(booking.getId())
                        .orElseThrow();

        assertEquals(
                BookingStatus.REJECTED,
                updated.getStatus()
        );
    }

    @Test
    void getById_shouldReturnBooking() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        booking = bookingRepository.save(booking);

        BookingDto result =
                bookingService.getById(
                        owner.getId(),
                        booking.getId()
                );

        assertEquals(
                booking.getId(),
                result.getId()
        );
    }

    @Test
    void getUserBookings_shouldReturnBookings() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        bookingRepository.save(booking);

        List<BookingDto> result =
                bookingService.getUserBookings(
                        booker.getId(),
                        BookingState.ALL
                );

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnBookings() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        bookingRepository.save(booking);

        List<BookingDto> result =
                bookingService.getOwnerBookings(
                        owner.getId(),
                        BookingState.ALL
                );

        assertEquals(1, result.size());
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(999L, dto)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void create_shouldThrowWhenItemNotFound() {

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(999L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booker.getId(), dto)
        );

        assertEquals("Предмет не найден", exception.getMessage());
    }

    @Test
    void create_shouldThrowWhenItemUnavailable() {

        item.setAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.create(booker.getId(), dto)
        );

        assertEquals(
                "Предмет недоступен для бронирования",
                exception.getMessage()
        );
    }

    @Test
    void getById_shouldThrowWhenBookingNotFound() {

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(
                        owner.getId(),
                        999L
                )
        );

        assertEquals(
                "Бронирование не найдено",
                exception.getMessage()
        );
    }

    @Test
    void approve_shouldThrowWhenUserIsNotOwner() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        Booking SavedBooking = bookingRepository.save(booking);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookingService.approve(
                        booker.getId(),
                        SavedBooking.getId(),
                        true
                )
        );

        assertEquals(
                "Менять статус может только владелец",
                exception.getMessage()
        );
    }

    @Test
    void approve_shouldThrowWhenBookingNotFound() {

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(
                        owner.getId(),
                        999L,
                        true
                )
        );

        assertEquals(
                "Бронирование не найдено",
                exception.getMessage()
        );
    }

    @Test
    void getOwnerBookings_shouldThrowWhenOwnerNotFound() {

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getOwnerBookings(
                        999L,
                        BookingState.ALL
                )
        );

        assertEquals(
                "Пользователь не найден",
                exception.getMessage()
        );
    }
}
