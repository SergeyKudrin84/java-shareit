package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toCommentDto_shouldMapAuthorName() {

        User author = new User();
        author.setId(1L);
        author.setName("Ivan");

        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDto dto = mapper.toCommentDto(comment);

        assertEquals("Ivan", dto.getAuthorName());
        assertEquals("Comment", dto.getText());
    }
}
