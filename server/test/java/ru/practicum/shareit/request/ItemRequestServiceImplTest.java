package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper mapper;
    @InjectMocks
    private ItemRequestServiceImpl service;

    @Test
    void create_shouldSaveRequest() {

        User user = new User();
        user.setId(1L);

        ItemRequest saved = new ItemRequest();
        saved.setId(1L);

        ItemRequestDto dtoResponse = new ItemRequestDto();
        dtoResponse.setId(1L);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need drill");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.save(any()))
                .thenReturn(saved);

        when(mapper.toRequestDto(saved))
                .thenReturn(dtoResponse);

        ItemRequestDto result = service.create(1L, createDto);

        assertEquals(1L, result.getId());

        verify(itemRequestRepository).save(any());
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need drill");

        assertThrows(
                NotFoundException.class,
                () -> service.create(1L, dto)
        );
    }

    @Test
    void getOwnRequests_shouldReturnRequests() {

        User user = new User();
        user.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));

        when(mapper.toRequestDtoList(any()))
                .thenReturn(List.of(dto));

        List<ItemRequestDto> result = service.getOwnRequests(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_shouldThrowWhenRequestNotFound() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getRequestById(1L, 1L)
        );
    }

    @Test
    void getAllRequests_shouldReturnRequests() {

        User user = new User();
        user.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(100L);

        ItemRequestDto dto =
                new ItemRequestDto();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));

        when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(List.of());

        when(mapper.toRequestDto(any()))
                .thenReturn(dto);

        List<ItemRequestDto> result = service.getAllRequests(1L);

        assertEquals(1, result.size());
    }
}
