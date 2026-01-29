package ru.practicum.explorewithme.service.comment;

import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentStatusDto;
import ru.practicum.explorewithme.dto.comment.UpdatedCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto getById(Long id);

    Collection<CommentDto> getCommentsForEvent(Long eventId, String sort, int from, int size);

    Collection<CommentDto> getCommentsByUserId(Long userId, String sort, int from, int size);

    // для админа
    Collection<CommentDto> getAllPendingComments(String sort, int from, int size);

    Collection<CommentDto> getAllPublishedComments(String sort, int from, int size);

    CommentDto create(Long userId, Long eventId, CommentDto commentDto);

    CommentDto update(Long userId, Long commentId, UpdatedCommentDto commentDto);

    void delete(Long userId, Long commentId);

    void deleteByAdmin(Long id);

    CommentDto updateStatus(Long commentId, CommentStatusDto statusDto);
}
