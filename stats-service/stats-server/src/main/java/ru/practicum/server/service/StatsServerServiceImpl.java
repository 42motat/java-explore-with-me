package ru.practicum.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.server.model.Hit;
import ru.practicum.server.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {
    private final StatsServerRepository statsServerRepository;

    @Override
    @Transactional
    public HitDto hit(HitDto hitDto) {
        Hit hit = statsServerRepository.save(HitMapper.mapToHit(hitDto));
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<StatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique != null && unique) {
            return statsServerRepository.findAllStatsUnique(start, end, uris);
        }
        return statsServerRepository.findAllStats(start, end, uris);
    }
}
