package ru.practicum.explorewithme.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.ReturnedEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.service.event.EventService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @GetMapping("/{userId}/events")
    public Collection<EventFullDto> getEventsByUser(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @RequestBody @Valid ReturnedEventDto returnedEventDto) {
        return eventService.create(userId, returnedEventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventRequest requestDto) {
        return eventService.update(userId, eventId, requestDto);
    }
}
