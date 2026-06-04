package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl service;

    @Test
    void addNewItem_shouldSaveItem() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setName("Drill");

        ItemDto dto = new ItemDto();
        dto.setName("Drill");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemMapper.toItem(dto))
                .thenReturn(item);

        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemMapper.toItemDto(item))
                .thenReturn(dto);

        ItemDto result = service.addNewItem(1L, dto);

        assertEquals("Drill", result.getName());

        verify(itemRepository).save(item);
    }

    @Test
    void addNewItem_withRequest_shouldSaveItem() {

        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(10L);

        Item item = new Item();
        item.setName("Drill");

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setRequestId(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        when(itemMapper.toItem(dto))
                .thenReturn(item);

        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemMapper.toItemDto(item))
                .thenReturn(dto);

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.addNewItem(1L, dto);

        ArgumentCaptor<Item> captor =
                ArgumentCaptor.forClass(Item.class);

        verify(itemRepository).save(captor.capture());

        Item result = captor.getValue();

        assertEquals("Drill", result.getName());
        assertEquals(owner, result.getOwner());
        assertEquals(request, result.getRequest());
    }

    @Test
    void addNewItem_shouldThrowWhenRequestNotFound() {

        User owner = new User();
        owner.setId(1L);

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setRequestId(10L);

        Item item = new Item();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(10L))
                .thenReturn(Optional.empty());

        when(itemMapper.toItem(dto))
                .thenReturn(item);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.addNewItem(1L, dto)
        );

        assertEquals("Запрос не найден", exception.getMessage());

        verify(itemRepository, never()).save(any());
    }

    @Test
    void addNewItem_shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addNewItem(1L, new ItemDto()));
    }

    @Test
    void updateItem_shouldThrowWhenNotOwner() {

        User owner = new User();
        owner.setId(99L);

        Item item = new Item();
        item.setOwner(owner);

        when(itemRepository.findByIdWithOwnerAndItemRequest(1L))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> service.updateItem(1L, 1L, new ItemDto()));
    }

    @Test
    void updateItem_shouldUpdateFields() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);
        item.setName("Old");

        ItemDto patch = new ItemDto();
        patch.setName("New");
        patch.setDescription("New description");
        patch.setAvailable(false);

        when(itemRepository.findByIdWithOwnerAndItemRequest(1L))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(itemMapper.toItemDto(any()))
                .thenReturn(patch);

        service.updateItem(1L, 1L, patch);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

        verify(itemRepository, atLeastOnce())
                .save(captor.capture());

        assertEquals("New", captor.getValue().getName());
        assertEquals("New description", captor.getValue().getDescription());
        assertFalse(captor.getValue().getAvailable());
    }

    @Test
    void searchItem_shouldReturnEmptyListForBlankText() {

        List<ItemDto> result = service.searchItem(" ");

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldThrowWhenNoBooking() {

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(),
                any()))
                .thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.addComment(
                        1L,
                        1L,
                        commentCreateDto
                ));
    }

    @Test
    void addComment_shouldSaveCommentAndReturnDto() {

        User author = new User();
        author.setId(1L);

        Item item = new Item();
        item.setId(10L);

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Excellent item");

        Comment savedComment = Comment.builder()
                .id(100L)
                .text("Excellent item")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDto resultDto = new CommentDto();
        resultDto.setId(100L);
        resultDto.setText("Excellent item");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));

        when(bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(
                        eq(1L),
                        eq(10L),
                        eq(BookingStatus.APPROVED),
                        any(LocalDateTime.class)))
                .thenReturn(true);

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        when(commentMapper.toCommentDto(savedComment))
                .thenReturn(resultDto);

        CommentDto result = service.addComment(1L, 10L, commentCreateDto);

        assertEquals(100L, result.getId());
        assertEquals("Excellent item", result.getText());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());

        Comment commentToSave = captor.getValue();

        assertEquals("Excellent item", commentToSave.getText());
        assertEquals(author, commentToSave.getAuthor());
        assertEquals(item, commentToSave.getItem());
        assertNotNull(commentToSave.getCreated());

        verify(commentMapper).toCommentDto(savedComment);
    }

    @Test
    void getItem_shouldThrowWhenItemNotFound() {

        when(itemRepository.findByIdWithOwnerAndItemRequest(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getItem(1L)
        );

        assertEquals(
                "Предмет не найден",
                exception.getMessage()
        );
    }

    @Test
    void getItem_shouldSetLastAndNextBooking() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Booking currentBooking = new Booking();
        currentBooking.setId(100L);
        currentBooking.setItem(item);
        currentBooking.setStart(
                LocalDateTime.now().minusDays(1)
        );
        currentBooking.setEnd(
                LocalDateTime.now().plusDays(1)
        );

        Booking futureBooking = new Booking();
        futureBooking.setId(200L);
        futureBooking.setItem(item);
        futureBooking.setStart(
                LocalDateTime.now().plusDays(5)
        );
        futureBooking.setEnd(
                LocalDateTime.now().plusDays(6)
        );

        ItemWithBookingDto dto =
                ItemWithBookingDto.builder()
                        .id(10L)
                        .build();

        BookingDto lastBookingDto = new BookingDto();
        lastBookingDto.setId(100L);

        BookingDto nextBookingDto = new BookingDto();
        nextBookingDto.setId(200L);

        when(itemRepository.findByIdWithOwnerAndItemRequest(10L))
                .thenReturn(Optional.of(item));

        when(bookingRepository
                .findByItemIdAndStatusOrderByStartAsc(
                        10L,
                        BookingStatus.APPROVED))
                .thenReturn(List.of(
                        currentBooking,
                        futureBooking
                ));

        when(itemMapper.toItemWithBookingDto(item))
                .thenReturn(dto);

        when(bookingMapper.toBookingDto(currentBooking))
                .thenReturn(lastBookingDto);

        when(bookingMapper.toBookingDto(futureBooking))
                .thenReturn(nextBookingDto);

        when(commentRepository.findByItemIdOrderByCreatedDesc(10L))
                .thenReturn(List.of());

        ItemWithBookingDto result =
                service.getItem(10L);

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());

        assertEquals(
                100L,
                result.getLastBooking().getId()
        );

        assertEquals(
                200L,
                result.getNextBooking().getId()
        );
    }
}
