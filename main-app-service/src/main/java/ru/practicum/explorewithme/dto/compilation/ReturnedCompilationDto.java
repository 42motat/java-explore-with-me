package ru.practicum.explorewithme.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReturnedCompilationDto {
    private Long id;

    private Boolean pinned = false;

    @NotBlank
    @Size(max = 50)
    private String title;

    private List<Long> events;
}
