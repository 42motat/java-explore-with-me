package ru.practicum.explorewithme.dto.comment;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.explorewithme.model.CommentStatus;

@Getter
@Setter
public class CommentStatusDto {
    private CommentStatus status;
}
