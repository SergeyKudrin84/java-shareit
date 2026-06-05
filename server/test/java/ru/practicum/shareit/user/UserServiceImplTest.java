package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserDto dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");

        dto = new UserDto();
        dto.setId(1L);
        dto.setName("Ivan");
        dto.setEmail("ivan@mail.ru");
    }

    @Test
    void saveUser_shouldSave() {
        when(mapper.toUser(dto)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserDto(user)).thenReturn(dto);

        UserDto result = service.saveUser(dto);

        assertEquals(dto, result);
        verify(repository).save(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(dto);
        UserDto result = service.getUserById(1L);
        assertEquals(dto, result);
    }

    @Test
    void getUserById_shouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUserById(1L));
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        UserDto patch = new UserDto();
        patch.setName("Petr");
        patch.setEmail("petr@mail.ru");

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class)))
                .thenReturn(user);

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName("Petr");
        updatedDto.setEmail("petr@mail.ru");

        when(mapper.toUserDto(any(User.class)))
                .thenReturn(updatedDto);

        UserDto result = service.updateUser(1L, patch);

        assertEquals("Petr", result.getName());
        assertEquals("petr@mail.ru", result.getEmail());
    }

    @Test
    void updateUser_shouldIgnoreBlankFields() {
        UserDto patch = new UserDto();
        patch.setName("");
        patch.setEmail(" ");

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class)))
                .thenReturn(user);

        when(mapper.toUserDto(user))
                .thenReturn(dto);

        UserDto result = service.updateUser(1L, patch);

        assertEquals("Ivan", result.getName());
        assertEquals("ivan@mail.ru", result.getEmail());
    }

    @Test
    void deleteUser_shouldCallRepository() {
        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> service.getUserById(99999L)
        );
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        UserDto dto = new UserDto();
        dto.setName("New Name");

        assertThrows(
                NotFoundException.class,
                () -> service.updateUser(99999L, dto)
        );
    }
}