package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.create(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        return UserMapper.toUserDto(userRepository.update(UserMapper.fromUserDto(userDto), id));
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }
}
