package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(" select c from Comment c " +
            " where c.status = 'PENDING' ")
    Collection<Comment> findAllPending(PageRequest pageRequest);

    @Query(" select c from Comment c " +
           " where c.status = 'CONFIRMED' ")
    Collection<Comment> findAllPublished(PageRequest pageRequest);

    Collection<Comment> findByEventId(Long eventId, PageRequest pageRequest);

    Collection<Comment> findAllByAuthorId(Long userId, PageRequest pageRequest);
}
