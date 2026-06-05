package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void deleteUser(Long userId) {
        log.info("Delete user with id {}", userId);
        userRepository.deleteById(userId);
        log.info("User with id {} deleted", userId);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Save user {}", userDto);
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("Saved user {}", savedUser);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Update user with id {}", userId);
        User existingUser = findById(userId);
        log.info("Existing user {}", existingUser);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user {}", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Get user with id {}", userId);
        User foundedUser = findById(userId);
        log.info("Founded user {}", foundedUser);
        return userMapper.toUserDto(foundedUser);
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String message = "Пользователь не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

}
