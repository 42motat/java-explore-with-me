package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean pinned;

    private String title;

    @ManyToMany
    @JoinTable(name = "event_compilations", joinColumns = @JoinColumn(name = "compilation_id"),
               inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
}
