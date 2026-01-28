package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.UpdatedCommentDto;
import ru.practicum.explorewithme.exception.BadRequest;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(UserMapper.mapToUserShortDto(comment.getAuthor()));
        commentDto.setEvent(comment.getEvent().getId());
        commentDto.setPublishedOn(comment.getPublishedOn());
        commentDto.setStatus(comment.getStatus());
        commentDto.setUpdatedOn(comment.getUpdatedOn());
        return commentDto;
    }

    public static Comment updateCommentFields(Comment comment, UpdatedCommentDto updatedCommentDto) {
        if (!updatedCommentDto.getText().isBlank()) {
            comment.setText(updatedCommentDto.getText());
        } else {
            throw new BadRequest("комментарий не может быть пустым");
        }
        comment.setUpdatedOn(LocalDateTime.now());
        return comment;
    }
}
