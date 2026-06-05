package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    @NotEmpty(message = "Название не может быть пустым")
    private String name;
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Доступность не может быть пустой")
    private Boolean available;
    private Long requestId;
}
