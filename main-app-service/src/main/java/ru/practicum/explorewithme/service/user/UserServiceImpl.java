package ru.practicum.explorewithme.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.exception.Conflict;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAllUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest)
                    .stream()
                    .toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest)
                    .stream()
                    .toList();
        }
        return users.stream()
                .map(UserMapper::mapToUserFullDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        validEmailCheck(user.getEmail(), user);
        userRepository.save(user);
        return UserMapper.mapToUserFullDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private void validEmailCheck(String email, User userToCheck) {
        boolean emailAlreadyExists = userRepository.findAll()
                .stream()
                .filter(user -> !user.getId().equals(userToCheck.getId()))
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailAlreadyExists) {
            throw new Conflict("Такая электронная почта уже используется");
        }
    }
}
