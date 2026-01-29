package ru.practicum.explorewithme.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentStatusDto;
import ru.practicum.explorewithme.dto.comment.UpdatedCommentDto;
import ru.practicum.explorewithme.exception.Conflict;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto getById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public Collection<CommentDto> getCommentsForEvent(Long eventId, String sort, int from, int size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        PageRequest pageRequest;
        if (sort == null) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        } else if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            throw new NotFoundException("Указанный вариант сортировки не найден");
        }

        Collection<Comment> comments = commentRepository.findByEventId(eventId, pageRequest);
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public Collection<CommentDto> getCommentsByUserId(Long userId, String sort, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        PageRequest pageRequest;
        if (sort == null) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        } else if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            throw new NotFoundException("Указанный вариант сортировки не найден");
        }

        Collection<Comment> comments = commentRepository.findAllByAuthorId(userId, pageRequest);
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public Collection<CommentDto> getAllPendingComments(String sort, int from, int size) {
        PageRequest pageRequest;
        if (sort == null) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        } else if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            throw new NotFoundException("Указанный вариант сортировки не найден");
        }

        Collection<Comment> comments = commentRepository.findAllPending(pageRequest);
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public Collection<CommentDto> getAllPublishedComments(String sort, int from, int size) {
        PageRequest pageRequest;
        if (sort == null) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        } else if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            throw new NotFoundException("Указанный вариант сортировки не найден");
        }

        Collection<Comment> comments = commentRepository.findAllPublished(pageRequest);
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new Conflict("Мероприятие ещё не опубликовано. Пожалуйста, дождитесь публикации");
        }

        Comment comment = CommentMapper.mapToComment(commentDto, user, event);
        comment.setPublishedOn(LocalDateTime.now());
        comment.setUpdatedOn(LocalDateTime.now());
        comment.setStatus(CommentStatus.PENDING);

        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, UpdatedCommentDto commentDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!commentDto.getAuthor().equals(userId)) {
            throw new Conflict("Только автор может редактировать комментарий");
        }

        // Редактирование комментария доступно автору комментария во всех статусах, поэтому проверки статуса нет.
        /* Логика:
        * - если комментарий опубликован, его можно редактировать
        * - если комментарий ещё НЕ опубликован, его можно редактировать, не дожидаясь одобрения администратора
        * - если комментарий отклонён администратором, его можно скорректировать
        *  */

        Comment updatedComment = CommentMapper.updateCommentFields(commentToUpdate, commentDto);

        commentRepository.save(updatedComment);

        return CommentMapper.mapToCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long id) {
        commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentDto updateStatus(Long commentId, CommentStatusDto statusDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        comment.setStatus(statusDto.getStatus());
        comment.setUpdatedOn(LocalDateTime.now());

        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }
}
