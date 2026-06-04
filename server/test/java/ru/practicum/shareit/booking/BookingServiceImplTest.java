package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl service;

    @Test
    void create_shouldSaveBooking() {

        User user = new User();
        user.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();

        Booking savedBooking = new Booking();
        savedBooking.setId(100L);

        BookingDto resultDto = new BookingDto();
        resultDto.setId(100L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findByIdWithOwnerAndItemRequest(10L))
                .thenReturn(Optional.of(item));

        when(bookingMapper.toBooking(dto))
                .thenReturn(booking);

        when(bookingRepository.save(any()))
                .thenReturn(savedBooking);

        when(bookingMapper.toBookingDto(savedBooking))
                .thenReturn(resultDto);

        BookingDto result = service.create(1L, dto);

        assertEquals(100L, result.getId());

        ArgumentCaptor<Booking> captor =
                ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).save(captor.capture());

        assertEquals(user, captor.getValue().getBooker());
        assertEquals(item, captor.getValue().getItem());
        assertEquals(
                BookingStatus.WAITING,
                captor.getValue().getStatus()
        );
    }

    @Test
    void create_shouldThrowWhenItemUnavailable() {

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findByIdWithOwnerAndItemRequest(1L))
                .thenReturn(Optional.of(item));

        assertThrows(
                IllegalStateException.class,
                () -> service.create(1L, dto)
        );
    }

    @Test
    void approve_shouldApproveBooking() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = new BookingDto();
        dto.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findByIdWithItemAndBooker(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(bookingMapper.toBookingDto(any()))
                .thenReturn(dto);

        BookingDto result =
                service.approve(1L, 1L, true);

        assertEquals(
                BookingStatus.APPROVED,
                booking.getStatus()
        );
    }

    @Test
    void approve_shouldRejectBooking() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);

        when(bookingRepository.findByIdWithItemAndBooker(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(bookingMapper.toBookingDto(any()))
                .thenReturn(new BookingDto());

        service.approve(1L, 1L, false);

        assertEquals(
                BookingStatus.REJECTED,
                booking.getStatus()
        );
    }

    @Test
    void approve_shouldThrowWhenNotOwner() {

        User owner = new User();
        owner.setId(5L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);

        when(bookingRepository.findByIdWithItemAndBooker(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(
                RuntimeException.class,
                () -> service.approve(1L, 1L, true)
        );
    }

    @Test
    void getById_shouldReturnBooking() {

        Booking booking = new Booking();
        booking.setId(1L);

        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingRepository.findByIdBelongOwnerOrBooker(1L, 1L))
                .thenReturn(Optional.of(booking));

        when(bookingMapper.toBookingDto(booking))
                .thenReturn(dto);

        BookingDto result =
                service.getById(1L, 1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getUserBookings_currentState() {

        when(bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                )
        ).thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.CURRENT
        );

        verify(bookingRepository)
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    void getUserBookings_pastState() {

        when(bookingRepository
                .findByBookerIdAndEndBeforeOrderByStartDesc(
                        anyLong(), any())
        ).thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.PAST
        );

        verify(bookingRepository)
                .findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getUserBookings_futureState() {

        when(bookingRepository
                .findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any())
        ).thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.FUTURE
        );

        verify(bookingRepository)
                .findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getUserBookings_waitingState() {

        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any())
        ).thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.WAITING
        );

        verify(bookingRepository)
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getUserBookings_rejectedState() {

        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any())
        ).thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.REJECTED
        );

        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(
                anyLong(),
                any()
        );
    }

    @Test
    void getUserBookings_allState() {

        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong())).
                thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getUserBookings(
                1L,
                BookingState.ALL
        );

        verify(bookingRepository).findByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getOwnerBookings_currentState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.CURRENT
        );

        verify(bookingRepository)
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    void getOwnerBookings_pastState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        anyLong(),
                        any()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.PAST
        );

        verify(bookingRepository)
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        anyLong(),
                        any()
                );
    }

    @Test
    void getOwnerBookings_futureState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.FUTURE
        );

        verify(bookingRepository)
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any()
                );
    }

    @Test
    void getOwnerBookings_waitingState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.WAITING
        );

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any()
                );
    }

    @Test
    void getOwnerBookings_rejectedState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.REJECTED
        );

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any()
                );
    }

    @Test
    void getOwnerBookings_allState() {

        User owner = new User();
        owner.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository
                .findByItemOwnerIdOrderByStartDesc(
                        anyLong()))
                .thenReturn(List.of());

        when(bookingRepository.findAllWithItemAndBookerByIds(any()))
                .thenReturn(List.of());

        when(bookingMapper.toBookingDtoList(any()))
                .thenReturn(List.of());

        service.getOwnerBookings(
                1L,
                BookingState.ALL
        );

        verify(bookingRepository)
                .findByItemOwnerIdOrderByStartDesc(
                        anyLong()
                );
    }
}
