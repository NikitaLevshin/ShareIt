package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.model.JsonModels.*;
import static ru.practicum.shareit.model.TestModels.*;

@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"ru.yandex.practicum.shareit"})
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> lists = userService.getAll();

        assertEquals(lists.size(), 1);
        assertEquals(lists.get(0).getName(), userName);
        assertEquals(lists.get(0).getEmail(), email);
    }

    @Test
    void addUserTest() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserDto userDto1 = userService.create(userDto);

        assertEquals(userDto1.getName(), userName);
        assertEquals(userDto1.getEmail(), email);
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDto userDto1 = userService.getById(user.getId());

        Assertions.assertEquals(userDto1.getName(), userName);
        Assertions.assertEquals(userDto1.getEmail(), email);
    }

    @Test
    void deleteUserTest() {
        assertDoesNotThrow(() -> userService.delete(user.getId()));
    }
}