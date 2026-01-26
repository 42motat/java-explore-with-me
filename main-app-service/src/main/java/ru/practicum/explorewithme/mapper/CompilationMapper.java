package ru.practicum.explorewithme.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.ReturnedCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.Event;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(Compilation compilation, List<EventShortDto> events) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(events);
        return compilationDto;
    }

    public static Compilation mapToCompilation(ReturnedCompilationDto compilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(events);
        return compilation;
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationDto compilationDto,
                                                      Set<Event> events) {
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(events);
        }
        return compilation;
    }
}
