package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(target = "authorName", qualifiedByName = "getAuthorNameFromAuthor", source = "author")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentCreateDto commentCreateDto);

    @Named("getAuthorNameFromAuthor")
    default String getNameFromAuthor(User author) {
        return author.getName();
    }
}
