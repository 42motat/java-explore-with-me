package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByCategoryId(Long categoryId);

    @Query(" select e from Event e " +
           " where (?1 is null or e.initiator.id in ?1) " +
           " and (?2 is null or e.state in ?2) " +
           " and (?3 is null or e.category.id in ?3) " +
           " and (e.eventDate >= ?4) " +
           " and (e.eventDate <= ?5) ")
    Collection<Event> findAllEvents(List<Long> userIds, List<EventState> states, List<Long> categories,
                                    LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    @Query(" select e from Event e " +
           " where e.state = 'PUBLISHED' " +
           " and (?1 is null or lower(e.annotation) like concat('%', cast(?1 as text), '%') or lower(e.description) like concat('%', cast(?1 as text), '%') ) " +
           " and (?2 is null or e.category.id in ?2) " +
           " and (?3 is null or e.paid = ?3) " +
           " and (e.eventDate >= ?4) " +
           " and (e.eventDate <= ?5) " +
           " and (?6 is null or ?6 = false " +
           " or e.participantLimit is null or e.participantLimit = 0 " +
           " or e.confirmedRequests < e.participantLimit ) ")
    Collection<Event> findPublishedEvents(String text, List<Long> categories, Boolean paid,
                                          LocalDateTime start, LocalDateTime end, Boolean available,
                                          PageRequest pageRequest);

    Collection<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);
}
