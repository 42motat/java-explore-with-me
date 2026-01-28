package ru.practicum.explorewithme.controller.admin;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentStatusDto;
import ru.practicum.explorewithme.service.comment.CommentService;

import java.util.Collection;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    public Collection<CommentDto> getAllCommentsOfEvent(@PathVariable Long eventId,
                                                        String sort,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getCommentsForEvent(eventId, sort, from, size);
    }

    @GetMapping("/users/{userId}")
    public Collection<CommentDto> getAllCommentsByUser(@PathVariable Long userId,
                                                       String sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getCommentsByUserId(userId, sort, from, size);
    }

    @GetMapping("/pending")
    public Collection<CommentDto> getAllPendingComments(String sort,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getAllPendingComments(sort, from, size);
    }

    @GetMapping("/published")
    public Collection<CommentDto> getAllPublishedComments(String sort,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getAllPublishedComments(sort, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateStatus(@PathVariable Long commentId,
                                   @RequestBody CommentStatusDto statusDto) {
        return commentService.updateStatus(commentId, statusDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commentId) {
        commentService.deleteByAdmin(commentId);
    }
}
