package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class Item {
    private int id;
    private String name;
    private String description;

    private Boolean available;
    private int owner;
    private ItemRequest request;

    public Item(int id, String name, String description, Boolean available, int owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}


