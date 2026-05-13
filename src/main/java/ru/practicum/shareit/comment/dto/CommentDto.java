package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
