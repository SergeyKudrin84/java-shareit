package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @NotEmpty(message = "Название не может быть пустым")
    private String name;
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Доступность не может быть пустой")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
