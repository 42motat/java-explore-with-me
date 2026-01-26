package ru.practicum.explorewithme.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.ReturnedEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.exception.BadRequest;
import ru.practicum.explorewithme.exception.Conflict;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Value("${application.name}")
    private String appName;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final StatsClient statsClient;

    @Override
    public EventFullDto getById(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
        log.warn("uri = {}, addr = {}", httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        statsClient.hit(httpServletRequest);

        Long views = getEventViews(id);
        log.warn("просмотры после запроса в клиент = {}", views);
        return EventMapper.mapToEventFullDto(event, views);
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        // СНАЧАЛА айди ивента, потом юзера
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
        Long views = getEventViews(eventId);
        return EventMapper.mapToEventFullDto(event, views);
    }

    @Override
    public Collection<EventFullDto> getAllEvents(List<Long> userIds, List<String> states, List<Long> categories,
                                                 LocalDateTime start, LocalDateTime end, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<EventState> eventStates;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .toList();
        } else {
            eventStates = null;
        }
        if (start == null) {
            start = LocalDateTime.now().minusDays(30);
        }
        if (end == null) {
            end = LocalDateTime.now().plusDays(30);
        }
        if (start.isAfter(end)) {
            throw new Conflict("Дата начала не может быть позже даты окончания");
        }
        Collection<Event> events = eventRepository
                .findAllEvents(userIds, eventStates, categories, start, end, pageRequest);

        return events.stream()
                .map((event -> EventMapper.mapToEventFullDto(event, getEventViews(event.getId()))))
                .toList();
    }

    @Override
    public Collection<EventFullDto> getEventsByUser(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, pageRequest)
                .stream()
                .map(event -> EventMapper.mapToEventFullDto(event, getEventViews(event.getId())))
                .toList();
    }

    @Override
    public Collection<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                        LocalDateTime start, LocalDateTime end, Boolean available,
                                                        String sort, int from, int size,
                                                        HttpServletRequest httpServletRequest) {
        // текст
        if (text != null) {
            text = text.toLowerCase();
        }

        // время
        if (start == null) {
            start = LocalDateTime.now().minusDays(30);
        }
        if (end == null) {
            end = LocalDateTime.now().plusDays(30);
        }
        if (start.isAfter(end)) {
            throw new BadRequest("Дата начала не может быть позже даты окончания");
        }

        // сортировка
        PageRequest pageRequest;
        if (sort == null) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        } else if (sort.equals("VIEWS")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("views").ascending());
        } else if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            throw new NotFoundException("Указанный вариант сортировки не найден");
        }

        Collection<Event> events = eventRepository.findPublishedEvents(text, categories, paid,
                                                                       start, end, available, pageRequest);
        statsClient.hit(httpServletRequest);

        return events.stream()
                .map(event -> EventMapper.mapToEventShortDto(event, getEventViews(event.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, ReturnedEventDto eventDto) {
        Event event = EventMapper.mapToEvent(eventDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        event.setInitiator(user);

        Category category = categoryRepository.findCategoryById(eventDto.getCategory())
                        .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        event.setCategory(category);

        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event, 0L);
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventRequest requestDto) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (eventToUpdate.getState() == EventState.PUBLISHED) {
            throw new Conflict("Событие может быть изменено только во время модерации");
        }

        if (requestDto.getEventDate() != null && requestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new BadRequest("Дата события не может быть раньше, чем через два часа от момента публикации");
        }

        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, requestDto);

        Category category = categoryRepository.findCategoryById(eventToUpdate.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        updatedEvent.setCategory(category);

        if (requestDto.getStateAction() == EventStateAction.SEND_TO_REVIEW) {
            updatedEvent.setState(EventState.PENDING);
        } else if (requestDto.getStateAction() == EventStateAction.CANCEL_REVIEW) {
            updatedEvent.setState(EventState.CANCELED);
        }

        eventRepository.save(updatedEvent);

        return EventMapper.mapToEventFullDto(updatedEvent, getEventViews(updatedEvent.getId()));
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventRequest requestDto) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));

        if (eventToUpdate.getState() == EventState.PUBLISHED && requestDto.getStateAction() == EventStateAction.REJECT_EVENT) {
            throw new Conflict("Событие можно отклонить, только если оно еще не опубликовано");
        }
        if (eventToUpdate.getState() != EventState.PENDING && requestDto.getStateAction() == EventStateAction.PUBLISH_EVENT) {
            throw new Conflict("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }

        if (requestDto.getEventDate() != null && requestDto.getEventDate().minusHours(1L).isBefore(LocalDateTime.now())) {
            throw new BadRequest("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, requestDto);

        if (requestDto.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(requestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            updatedEvent.setCategory(category);
        }

        if (requestDto.getStateAction() == EventStateAction.PUBLISH_EVENT) {
            updatedEvent.setState(EventState.PUBLISHED);
        } else if (requestDto.getStateAction() == EventStateAction.REJECT_EVENT) {
            updatedEvent.setState(EventState.CANCELED);
        }

        eventRepository.save(updatedEvent);

        return EventMapper.mapToEventFullDto(updatedEvent, getEventViews(updatedEvent.getId()));
    }

    private Long getEventViews(Long id) {
        List<String> uris = List.of("/events/" + id);
        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        List<StatsDto> stats = statsClient.getStats(start, end, uris, true).getBody();

        if (stats == null || stats.isEmpty()) {
            return 0L;
        } else {
            return stats.getFirst().getHits();
        }
    }

}
