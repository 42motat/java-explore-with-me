package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsServerRepository extends JpaRepository<Hit, Long> {

    @Query(" select new ru.practicum.dto.StatsDto(h.app, h.uri, count(h.ip)) " +
           " from Hit h " +
           " where (h.timestamp between ?1 and ?2) " +
           " and (?3 is null or h.uri in ?3) " +
           " group by h.app, h.uri " +
           " order by count(h.ip) desc ")
    List<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(" select new ru.practicum.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            " from Hit h " +
            " where (h.timestamp between ?1 and ?2) " +
            " and (?3 is null or h.uri in ?3) " +
            " group by h.app, h.uri " +
            " order by count(h.ip) desc ")
    List<StatsDto> findAllStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);


}
