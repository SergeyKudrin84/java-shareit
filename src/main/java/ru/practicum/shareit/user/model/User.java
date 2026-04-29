package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id;
    private String name;
    @NotEmpty(message = "Электронная почта не может быть пустой и должна содержать символ '@'")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ '@'")
    private String email;
}
