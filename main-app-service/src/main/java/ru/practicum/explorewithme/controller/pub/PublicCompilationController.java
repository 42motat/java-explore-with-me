package ru.practicum.explorewithme.controller.pub;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.service.compilation.CompilationService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compilationId}")
    public CompilationDto getById(@PathVariable Long compilationId) {
        return compilationService.getById(compilationId);
    }

    @GetMapping
    public Collection<CompilationDto> getAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }
}
