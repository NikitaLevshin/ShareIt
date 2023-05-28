package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotNullException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User create(User user) {
        validate(user);
        id++;
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен пользователь {}.", user.getName());
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        log.info("Запрос на получение пользователя с id {}", id);
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Ошибка в получении пользователя");
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public User update(User user, int id) {
        log.info("Обновляем пользователя {}", user.getName());
        user.setId(id);
        validate(user);
        if (user.getName() == null) {
            user.setName(getById(id).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(getById(id).getEmail());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(int id) {
        if (users.containsKey(id)) {
            log.info("Удаляем пользователя с id {}", id);
            users.remove(id);
        } else {
            log.warn("Ошибка в удалении пользователя");
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    private void validate(User user) {
        if (!users.containsKey(user.getId())) {
            if (user.getName() == null) {
                throw new NotNullException(HttpStatus.BAD_REQUEST, "Имя пользователя не может быть пустым");
            } else if (user.getEmail() == null) {
                throw new NotNullException(HttpStatus.BAD_REQUEST, "Почта не может быть пустой");
            }
        }
        if (!users.isEmpty()) {
            for (Map.Entry<Integer, User> userEntry : users.entrySet()) {
                if (userEntry.getValue().getEmail().equalsIgnoreCase(user.getEmail()) && userEntry.getKey() != user.getId()) {
                    throw new ValidationException("Пользователь с таким email уже существует");
                } else if (userEntry.getValue().getName().equalsIgnoreCase(user.getName()) && userEntry.getKey() != user.getId()) {
                    throw new ValidationException(("Пользователь с таким именем уже существует"));
                }
            }
        }
    }
}
