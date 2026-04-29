package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Validator validator;

    @Override
    public void deleteUser(Long userId) {
        log.info("Delete user with id: {}", userId);
        users.removeIf(u -> u.getId().equals(userId));
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving user {}", user);
        validate(user);
        user.setId(idGenerator.incrementAndGet());
        users.add(user);
        log.info("User {} has been saved", user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        log.info("Updating user {}", user);
        User existingUser = getUserById(userId);
        log.debug("User before update: {}", existingUser);
        User updatedUser = new User();
        updatedUser.setId(existingUser.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            updatedUser.setName(existingUser.getName());
        } else {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            updatedUser.setEmail(existingUser.getEmail());
        } else {
            updatedUser.setEmail(user.getEmail());
        }
        validate(updatedUser);
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        log.info("User {} has been updated", existingUser);
        return existingUser;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Getting user by id {}", userId);
        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("User {} has been get", user);
        return user;
    }

    public void validate(User user) throws RuntimeException {

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            log.warn(message);
            throw new RuntimeException(message);
        }

        boolean emailExists = users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())
                        && (user.getId() == null || !u.getId().equals(user.getId())));

        if (emailExists) {
            String message = "Email уже занят";
            log.warn(message);
            throw new RuntimeException(message);
        }

    }

}
