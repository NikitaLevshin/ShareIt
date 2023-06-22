package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    List<ItemDto> getAllByOwner(int ownerId);

    ItemDto getById(int id);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto itemDto, int ownerId);

    ItemDto update(ItemDto itemDto, int ownerId, int id);

    void delete(int id);

}
