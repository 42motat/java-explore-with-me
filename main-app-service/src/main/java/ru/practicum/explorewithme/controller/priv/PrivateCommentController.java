package ru.practicum.explorewithme.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.UpdatedCommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;

import java.util.Collection;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateCommentController {
    private final CommentService commentService;

    @GetMapping("/comments")
    public Collection<CommentDto> getPublishedComments(String sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getAllPublishedComments(sort, from, size);
    }

    // создание комментария для определённого ивента
    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @RequestBody @Valid CommentDto commentDto) {
        return commentService.create(userId, eventId, commentDto);
    }

    // обновление и удаления комментария не требует поиска ивента, а только айди (для упрощения)
    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto update(@PathVariable Long userId,
                             @PathVariable Long commentId,
                             @RequestBody @Valid UpdatedCommentDto commentDto) {
        return commentService.update(userId, commentId, commentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        commentService.delete(userId, commentId);
    }


}
