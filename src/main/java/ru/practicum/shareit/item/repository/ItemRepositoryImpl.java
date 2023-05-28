package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotNullException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public List<Item> getAllByOwner(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        } else {
            return items.values().stream()
                    .filter(item -> (item.getDescription().toLowerCase().contains(text.toLowerCase()))
                            || item.getName().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Item create(Item item, int ownerId) {
        validate(item);
        id++;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item, int ownerId, int id) {
        item.setId(id);
        if (items.get(id).getOwner() != ownerId) {
            throw new NotNullException(HttpStatus.NOT_FOUND, "Вещь может обновить только владелец");
        }
        if (item.getName() == null) {
            item.setName(items.get(id).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(id).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(id).getAvailable());
        }
        items.put(id, item);
        return item;
    }

    @Override
    public void delete(int id) {
        items.remove(id);
    }

    private void validate(Item item) {
        try {
            userRepository.getById(item.getOwner());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        if (!items.containsKey(item.getId())) {
            if (item.getName().isBlank() || item.getName() == null
                    || item.getDescription() == null) {
                throw new NotNullException(HttpStatus.BAD_REQUEST, "Поля имя, описание и доступность не могут быть пустыми");
            }
            if (item.getAvailable() == null) {
                throw new NotNullException(HttpStatus.BAD_REQUEST, "Поле available не может быть пустым");
            }
        }
    }
}
