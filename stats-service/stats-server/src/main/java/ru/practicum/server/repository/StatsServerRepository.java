package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface StatsServerRepository extends JpaRepository<Hit, Long> {

    @Query(" select h.app, h.uri, count(h.ip) " +
           " from hits h " +
           " where h.timestamp between ?1 and ?2 " +
           " group by h.app, h.uri " +
           " order by count(h.ip) desc ")
    Collection<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end);

    @Query(" select h.app, h.uri, count(distinct h.ip) " +
            " from hits h " +
            " where h.timestamp between ?1 and ?2 " +
            " group by h.app, h.uri " +
            " order by count(h.ip) desc ")
    Collection<StatsDto> findAllStatsUnique(LocalDateTime start, LocalDateTime end);


}
