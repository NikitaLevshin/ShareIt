package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> getAll();

    User getById(int id);

    User update(User user, int id);

    void delete(int id);
}
