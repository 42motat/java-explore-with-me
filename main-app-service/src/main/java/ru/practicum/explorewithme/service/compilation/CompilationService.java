package ru.practicum.explorewithme.service.compilation;

import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.ReturnedCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationDto;

import java.util.Collection;

public interface CompilationService {
    CompilationDto getById(Long id);

    Collection<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto create(ReturnedCompilationDto compilationDto);

    CompilationDto update(Long id, UpdateCompilationDto compilationDto);

    void delete(Long id);
}
