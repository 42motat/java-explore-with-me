package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.Request;
import ru.practicum.explorewithme.model.RequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findByIdIn(List<Long> requestIds);

    Collection<Request> findByEventId(Long eventId);

    Collection<Request> findByRequesterId(Long userId);

    Integer countConfirmedRequestsByEventIdAndStatus(Long eventId, RequestStatus status);
}
