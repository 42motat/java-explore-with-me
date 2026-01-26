package ru.practicum.explorewithme.service.user;

import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<UserDto> findAllUsers(List<Long> ids, int from, int size);

    UserDto create(UserDto userDto);

    void delete(Long userId);
}
