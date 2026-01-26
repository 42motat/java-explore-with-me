package ru.practicum.explorewithme.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
public class CompilationDto {
    private Long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
