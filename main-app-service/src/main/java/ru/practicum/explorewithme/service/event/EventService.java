package ru.practicum.explorewithme.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.ReturnedEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto getById(Long id, HttpServletRequest httpServletRequest);

    EventFullDto getEventByUser(Long userId, Long eventId);

    Collection<EventFullDto> getAllEvents(List<Long> userIds, List<String> states, List<Long> categories,
                                          LocalDateTime start, LocalDateTime end, int from, int size);

    Collection<EventFullDto> getEventsByUser(Long userId, int from, int size);

    Collection<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime start, LocalDateTime end, Boolean available,
                                                 String sort, int from, int size, HttpServletRequest httpServletRequest);

    EventFullDto create(Long userId, ReturnedEventDto eventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventRequest requestDto);

    EventFullDto updateByAdmin(Long eventId, UpdateEventRequest requestDto);
}
