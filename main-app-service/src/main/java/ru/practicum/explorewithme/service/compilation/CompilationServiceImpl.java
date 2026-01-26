package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.ReturnedCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    public CompilationDto getById(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));

        List<EventShortDto> eventDtos = compilation.getEvents()
                .stream()
                .map(event -> EventMapper.mapToEventShortDto(event, getEventViews(event.getId())))
                .toList();

        return CompilationMapper.mapToCompilationDto(compilation, eventDtos);
    }

    @Override
    public Collection<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest).stream().toList();
        } else {
            compilations = compilationRepository.findAll(pageRequest).stream().toList();
        }

        return compilations.stream()
                .map(compilation -> {
                    return CompilationMapper.mapToCompilationDto(compilation, compilation.getEvents()
                            .stream()
                            .map(event -> EventMapper.mapToEventShortDto(event, getEventViews(event.getId())))
                            .toList());
                })
                .toList();
    }

    @Override
    @Transactional
    public CompilationDto create(ReturnedCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            compilationDto.getEvents().forEach(event -> events.add(getEventById(event)));
        }
        Compilation compilation = CompilationMapper.mapToCompilation(compilationDto, events);

        compilationRepository.save(compilation);

        List<EventShortDto> eventDtos = events.stream()
                .map(event -> EventMapper.mapToEventShortDto(event, getEventViews(event.getId())))
                .toList();

        return CompilationMapper.mapToCompilationDto(compilation, eventDtos);
    }

    @Override
    @Transactional
    public CompilationDto update(Long id, UpdateCompilationDto compilationDto) {
        Compilation compilationToCheck = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            compilationDto.getEvents().forEach(event -> events.add(getEventById(event)));
        }
        CompilationMapper.updateCompilationFields(compilationToCheck, compilationDto, events);

        compilationRepository.save(compilationToCheck);

        List<EventShortDto> eventDtos = events.stream()
                .map(event -> EventMapper.mapToEventShortDto(event, getEventViews(event.getId())))
                .toList();

        return CompilationMapper.mapToCompilationDto(compilationToCheck, eventDtos);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));
        compilationRepository.deleteById(id);
    }

    private Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        return event;
    }

    private Long getEventViews(Long id) {
        List<String> uris = List.of("/events" + id);
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        List<StatsDto> stats = statsClient.getStats(start, end, uris, true).getBody();

        if (stats == null || stats.isEmpty()) {
            return 0L;
        } else {
            return stats.getFirst().getHits();
        }
    }
}
