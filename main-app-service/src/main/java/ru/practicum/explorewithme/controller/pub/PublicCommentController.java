package ru.practicum.explorewithme.controller.pub;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;

import java.util.Collection;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public Collection<CommentDto> getPublishedComments(String sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return commentService.getAllPublishedComments(sort, from, size);
    }
}
