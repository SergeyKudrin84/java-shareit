package ru.practicum.shareit.request;

import org.springframework.test.context.ActiveProfiles;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");

        user = userRepository.save(user);
    }

    @Test
    void create_shouldSaveRequest() {

        ItemRequestCreateDto dto = new ItemRequestCreateDto();

        dto.setDescription("Need drill");

        ItemRequestDto saved = service.create(user.getId(), dto);

        ItemRequest request = requestRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(
                "Need drill",
                request.getDescription()
        );
    }

    @Test
    void getOwnRequests_shouldReturnUserRequests() {

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        requestRepository.save(request);

        List<ItemRequestDto> result = service.getOwnRequests(user.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_shouldReturnRequest() {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need drill");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = requestRepository.save(itemRequest);

        ItemRequestDto result = service.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(
                itemRequest.getId(),
                result.getId()
        );
    }

    @Test
    void getAllRequests_shouldReturnRequests() {

        User another = new User();
        another.setName("Petr");
        another.setEmail("petr@mail.ru");

        another = userRepository.save(another);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(another);
        request.setCreated(LocalDateTime.now());

        requestRepository.save(request);

        List<ItemRequestDto> result = service.getAllRequests(user.getId());

        assertEquals(1, result.size());
    }
}
