package ru.practicum.explorewithme.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.RequestDto;
import ru.practicum.explorewithme.service.request.RequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public Collection<RequestDto> getAllUserRequests(@PathVariable Long userId) {
        return requestService.getRequestsByUserInOtherEvents(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getEventRequestByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getEventRequestsByUser(userId, eventId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult update(@PathVariable Long userId, @PathVariable Long eventId,
                                                 @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {
        return requestService.update(userId, eventId, requestDto);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
