package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventShortDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    private Integer confirmedRequests;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    @Size(min = 3, max = 120)
    private String title;

    private Long views;
}
