package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.RequestDto;
import ru.practicum.explorewithme.exception.Conflict;
import ru.practicum.explorewithme.exception.Forbidden;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.RequestMapper;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Collection<RequestDto> getEventRequestsByUser(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new Forbidden("Пользователь не является инициатором события");
        }

        Collection<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    public Collection<RequestDto> getRequestsByUserInOtherEvents(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));
        Collection<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!requestRepository.findByRequesterId(userId).isEmpty()) {
            throw new Conflict("Можно добавить только один запрос на участие в мероприятии");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new Conflict("Инициатор не может добавить запрос на участие в своём мероприятии");
        }
        if (event.getParticipantLimit() > 0) {
            Integer confirmedParticipants = requestRepository.countConfirmedRequestsByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            log.warn("подтвержденные = {}, лимит = {}", confirmedParticipants, event.getParticipantLimit());
            if (confirmedParticipants >= event.getParticipantLimit()) {
                throw new Conflict("Достигнут лимит участников мероприятия");
            }
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new Conflict("Мероприятие ещё не опубликовано. Пожалуйста, дождитесь публикации");
        }

        Request request = RequestMapper.mapToRequest(user, event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        requestRepository.save(request);

        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new Forbidden("Пользователь не является владельцем события");
        }

        Collection<Request> requests = requestRepository.findByIdIn(requestDto.getRequestIds());
        if (requests.stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            throw new Conflict("Все запросы должны быть в статусе \"Ожидается\"");
        }

        RequestStatus status = requestDto.getStatus();

        EventRequestStatusUpdateResult resultDto = new EventRequestStatusUpdateResult();

        if (status.equals(RequestStatus.CONFIRMED)) {
            if (event.getConfirmedRequests() + requests.size() > event.getParticipantLimit()) {
                throw new Conflict("Лимит участников достигнут");
            } else {
                requests.forEach(request -> request.setStatus(status));
                event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
                eventRepository.save(event);
                requestRepository.saveAll(requests);
                log.warn("запросы на участие приняты = {}", requests.size());
                resultDto.getConfirmedRequests().addAll(requests.stream().map(RequestMapper::mapToRequestDto).toList());
            }
        } else if (status.equals(RequestStatus.REJECTED)) {
            requests.forEach(request -> request.setStatus(status));
            requestRepository.saveAll(requests);
            log.warn("запросы на участие отвергнуты = {}", requests.size());
            resultDto.getRejectedRequests().addAll(requests.stream().map(RequestMapper::mapToRequestDto).toList());
        }
        return resultDto;
    }

    @Override
    @Transactional
    public RequestDto cancel(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие не найден"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new Conflict("Пользователь не является инициатором запроса");
        }
        RequestStatus status = request.getStatus();
        Event event = eventRepository.findById(request.getEvent().getId())
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
        if (status == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return RequestMapper.mapToRequestDto(request);
    }
}
