package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("Saving new user {}", userDto);
        return userClient.saveUser(userDto);
    }

    @DeleteMapping({"/{userId}"})
    public ResponseEntity<Object>  deleteUser(@PathVariable Long userId) {
        log.info("Deleting user {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping({"/{userId}"})
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Getting user {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping({"/{userId}"})
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                              @RequestBody UserDto userDto) {
        log.info("Updating user {} with userId {}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }
}
