package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");

        user = repository.save(user);
    }

    @Test
    void saveUser_shouldPersistUser() {

        UserDto dto = new UserDto();
        dto.setName("Petr");
        dto.setEmail("petr@mail.ru");

        UserDto saved = userService.saveUser(dto);

        Optional<User> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Petr", found.get().getName());
        assertEquals("petr@mail.ru", found.get().getEmail());
    }

    @Test
    void getUserById_shouldReturnUser() {

        UserDto result = userService.getUserById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals("Ivan", result.getName());
        assertEquals("ivan@mail.ru", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {

        assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(99999L)
        );
    }

    @Test
    void updateUser_shouldUpdateName() {

        UserDto patch = new UserDto();
        patch.setName("Petr");

        userService.updateUser(user.getId(), patch);

        User updated = repository.findById(user.getId())
                .orElseThrow();

        assertEquals("Petr", updated.getName());
        assertEquals("ivan@mail.ru", updated.getEmail());
    }

    @Test
    void updateUser_shouldUpdateEmail() {

        UserDto patch = new UserDto();
        patch.setEmail("new@mail.ru");

        userService.updateUser(user.getId(), patch);

        User updated = repository.findById(user.getId())
                .orElseThrow();

        assertEquals("Ivan", updated.getName());
        assertEquals("new@mail.ru", updated.getEmail());
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {

        UserDto patch = new UserDto();
        patch.setName("Petr");

        assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(99999L, patch)
        );
    }

    @Test
    void deleteUser_shouldDeleteUser() {

        Long userId = user.getId();

        userService.deleteUser(userId);

        assertFalse(repository.findById(userId).isPresent());
    }
}