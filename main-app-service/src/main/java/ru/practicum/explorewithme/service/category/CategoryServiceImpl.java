package ru.practicum.explorewithme.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.exception.Conflict;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> new NotFoundException("Указанная категория не существует"));
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public Collection<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        Optional<Category> categoryChecked = categoryRepository.findCategoryByName(categoryDto.getName());
        if (categoryChecked.isPresent()) {
            throw new Conflict("Категория с таким названием уже существует");
        }
        Category category = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category categoryCheckedById = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена"));
        if (!categoryCheckedById.getName().equals(categoryDto.getName())) {
            Optional<Category> categoryCheckedByName = categoryRepository.findCategoryByName(categoryDto.getName());
            if (categoryCheckedByName.isPresent()) {
                throw new Conflict("Категория с таким названием уже существует");
            }
            categoryCheckedById.setName(categoryDto.getName());
        }
        categoryRepository.save(categoryCheckedById);
        return CategoryMapper.mapToCategoryDto(categoryCheckedById);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        categoryRepository.findCategoryById(id)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена"));
        if (eventRepository.findByCategoryId(id).isPresent()) {
            throw new Conflict("Категория не может быть удалена, поскольку содержит связанные события");
        }
        categoryRepository.deleteById(id);
    }
}
