package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(Long userId, User user);

    User getUserById(Long userId);

    void deleteUser(Long userId);
}
