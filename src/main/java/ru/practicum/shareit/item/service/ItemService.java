package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBooking;

import java.util.List;

public interface ItemService {
    List<ItemBooking> getAllByOwner(int ownerId);

    Item getById(int id);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto itemDto, int ownerId);

    ItemDto update(ItemDto itemDto, int ownerId, int id);

    void delete(int id);

    ItemBooking getItem(int itemId, int userId);

    CommentDto addComment(int itemId, CommentDto commentDto, int authorId);
}
