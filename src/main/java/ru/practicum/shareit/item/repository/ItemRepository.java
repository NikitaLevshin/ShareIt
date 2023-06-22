package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
        List<Item> findByOwnerId(int userId);

        Item findById(int userId);

        List<Item> findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
