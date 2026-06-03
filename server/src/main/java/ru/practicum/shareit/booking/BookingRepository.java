package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            select b
            from Booking b
            join fetch b.item as i
            join fetch i.owner
            join fetch b.booker
            where b.id = ?1
            """)
    Optional<Booking> findByIdWithItemAndBooker(Long bookingId);

    @Query("""
            select b
            from Booking b
            join fetch b.item as i
            join fetch i.owner as o
            join fetch b.booker as booker
            where o.id = ?1 or booker.id = ?1 or b.id = ?2
            """)
    Optional<Booking> findByIdBelongOwnerOrBooker(Long userId, Long id);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId,
            LocalDateTime time
    );

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime time
    );

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long bookerId,
            BookingStatus status
    );

    @Query("""
            select b
            from Booking b
            join fetch b.item as i
            join fetch i.owner
            join fetch b.booker
            where b.id in ?1
            order by b.start desc
            """)
    List<Booking> findAllWithItemAndBookerByIds(List<Long> bookingIds);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId,
            LocalDateTime time
    );

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime time
    );

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId,
            BookingStatus status
    );

    List<Booking> findByItemIdAndStatusOrderByStartAsc(
            Long itemId,
            BookingStatus status
    );

    List<Booking> findByItemIdInAndStatusOrderByStartAsc(
            List<Long> itemIds,
            BookingStatus status
    );

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime time
    );
}
