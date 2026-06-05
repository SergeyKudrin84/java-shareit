package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        log.info("Approve booking with id {} approved {}", bookingId, approved);
        Booking booking = findBookingById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Менять статус может только владелец");
        }
        booking.setStatus(
                approved
                        ? BookingStatus.APPROVED
                        : BookingStatus.REJECTED
        );
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(bookingSaved);
    }

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        log.info("Create booking {}", dto);
        User booker = findUserById(userId);
        Item item = findItemById(dto.getItemId());
        if (!item.getAvailable()) {
            throw new IllegalStateException("Предмет недоступен для бронирования");
        }
        Booking booking = bookingMapper.toBooking(dto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(bookingSaved);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        log.info("Get booking {}", bookingId);
        Booking booking = bookingRepository.findByIdBelongOwnerOrBooker(userId, bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        log.info("Get booking {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        log.info("Get user {} bookings {}", userId, state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {

            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId,
                    now,
                    now);

            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                    userId,
                    now);

            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                    userId,
                    now);

            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId,
                    BookingStatus.WAITING);

            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId,
                    BookingStatus.REJECTED);

            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        List<Long> bookingIds = bookings.stream()
                .map(Booking::getId)
                .toList();

        bookings = bookingRepository.findAllWithItemAndBookerByIds(bookingIds);
        log.info("Found user bookings {}", bookings.size());
        return bookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        log.info("Get owner {} bookings {}", ownerId, state);
        findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {

            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    ownerId,
                    now,
                    now);

            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                    ownerId,
                    now);

            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                    ownerId,
                    now);

            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    BookingStatus.WAITING);

            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    BookingStatus.REJECTED);

            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        };

        List<Long> bookingIds = bookings.stream()
                .map(Booking::getId)
                .toList();

        bookings = bookingRepository.findAllWithItemAndBookerByIds(bookingIds);
        log.info("Found owner bookings {}", bookings.size());
        return bookingMapper.toBookingDtoList(bookings);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String message = "Пользователь не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findByIdWithOwnerAndItemRequest(itemId)
                .orElseThrow(() -> {
                    String message = "Предмет не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findByIdWithItemAndBooker(bookingId)
                .orElseThrow(() -> {
                    String message = "Бронирование не найдено";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }
}
