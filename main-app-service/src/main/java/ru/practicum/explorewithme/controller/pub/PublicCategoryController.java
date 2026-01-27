package ru.practicum.explorewithme.controller.pub;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.service.category.CategoryService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public CategoryDto getById(@PathVariable Long categoryId) {
        return categoryService.getById(categoryId);
    }

    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return categoryService.getAllCategories(from, size);
    }
}
