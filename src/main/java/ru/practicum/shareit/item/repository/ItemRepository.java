package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllByOwner(int ownerId);

    Item getById(int id);

    List<Item> search(String text);

    Item create(Item item, int ownerId);

    Item update(Item item, int ownerId, int id);

    void delete(int id);
}
