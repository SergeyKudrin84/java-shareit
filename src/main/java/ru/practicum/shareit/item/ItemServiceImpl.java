package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        log.info("Adding new item {}", itemDto);
        User owner = findUserById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequest().getId())
                    .orElseThrow(() ->
                            new NotFoundException("Запрос не найден"));
            item.setRequest(itemRequest);
        }
        Item savedItem = itemRepository.save(item);
        log.info("Saved item {}", savedItem);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Updating item {} by user id {}", itemDto, userId);
        Item existingItem = findByIdWithOwnerAndItemRequest(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException(
                    "Только владелец может менять предмет"
            );
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        Item savedItem = itemRepository.save(existingItem);
        log.info("Updated item {}", savedItem);
        return itemMapper.toItemDto(itemRepository.save(savedItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingDto getItem(Long itemId) {
        log.info("Getting item {}", itemId);
        Item existingItem = findByIdWithOwnerAndItemRequest(itemId);

        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStartAsc(itemId,
                BookingStatus.APPROVED);
        Booking[] lastAndNextBookings = findLastAndNextBookingsForItem(bookings, itemId);
        ItemWithBookingDto itemWithBookingDto = itemMapper.toItemWithBookingDto(existingItem);
        itemWithBookingDto.setLastBooking(bookingMapper.toBookingDto(lastAndNextBookings[0]));
        itemWithBookingDto.setNextBooking(bookingMapper.toBookingDto(lastAndNextBookings[1]));
        itemWithBookingDto.setComments(getCommentsForItem(itemId));
        return itemWithBookingDto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Searching items by text {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> listOfItems = itemRepository.searchByTextInNameOrDescription(text);
        log.info("Founded items by text {}", listOfItems.size());
        return itemMapper.toItemDtoList(itemRepository.searchByTextInNameOrDescription(text));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingDto> getAllByOwner(Long userId) {
        log.info("Getting all items by owner {}", userId);
        findUserById(userId);
        List<Item> listOfItems = itemRepository.findByOwnerId(userId);

        List<Long> itemIds = listOfItems.stream()
                .map(Item::getId)
                .toList();
        List<Comment> allComments = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);
        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(
                                commentMapper::toCommentDto,
                                Collectors.toList()
                        )
                ));

        List<Booking> allBookings = bookingRepository.findByItemIdInAndStatusOrderByStartAsc(itemIds,
                BookingStatus.APPROVED);

        log.info("Founded items by owner {}", listOfItems.size());
        return listOfItems.stream()
                .map(item -> {
                    Long itemId = item.getId();
                    Booking[] lastAndNextBookings = findLastAndNextBookingsForItem(allBookings, itemId);
                    ItemWithBookingDto itemWithBookingDto = itemMapper.toItemWithBookingDto(item);
                    itemWithBookingDto.setLastBooking(bookingMapper.toBookingDto(lastAndNextBookings[0]));
                    itemWithBookingDto.setNextBooking(bookingMapper.toBookingDto(lastAndNextBookings[1]));
                    itemWithBookingDto.setComments(commentsByItemId.getOrDefault(itemId, List.of()));
                    return itemWithBookingDto;
                })
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String message = "Пользователь не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

    private Item findByIdWithOwnerAndItemRequest(Long itemId) {
        return itemRepository.findByIdWithOwnerAndItemRequest(itemId)
                .orElseThrow(() -> {
                    String message = "Предмет не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

    private Booking[] findLastAndNextBookingsForItem(
            List<Booking> allBookings,
            Long itemId) {
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> bookingsByItemId = allBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getItem().getId()
                ));

        List<Booking> itemBookings = bookingsByItemId.getOrDefault(itemId, List.of());

        Booking lastBooking = null;
        Booking nextBooking = null;

        for (Booking booking : itemBookings) {
            if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                lastBooking = booking;
            }
            if (booking.getStart().isAfter(now)) {
                nextBooking = booking;
                break;
            }
        }
        Booking[] bookingArray = new Booking[2];
        bookingArray[0] = lastBooking;
        bookingArray[1] = nextBooking;
        return bookingArray;
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        List<CommentDto> comments = commentRepository
                .findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
        return comments;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {

        User author = findUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new IllegalStateException("Пользователь не арендовал вещь");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }
}
