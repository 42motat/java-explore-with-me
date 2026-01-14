package ru.practicum.server.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsServerService {
    HitDto hit(HitDto hitDto);

    Collection<StatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
