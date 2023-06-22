package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с таким id не найден")));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        log.info("Добавлен пользователь {}.", userDto.getName());
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        log.info("Обновляем пользователя c id {}", id);
        User user = UserMapper.fromUserDto(getById(id));
        User updatedUser = UserMapper.fromUserDto(userDto);
        updatedUser.setId(id);
        if (updatedUser.getName() == null) {
            updatedUser.setName(user.getName());
        }
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void delete(int id) {
        log.info("Удаляем пользователя с id {}", id);
        userRepository.deleteById(id);
    }
}
