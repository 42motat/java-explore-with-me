package ru.practicum.explorewithme.service.request;

import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.RequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<RequestDto> getEventRequestsByUser(Long userId, Long eventId);

    Collection<RequestDto> getRequestsByUserInOtherEvents(Long userId);

    RequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto);

    RequestDto cancel(Long userId, Long requestId);
}
