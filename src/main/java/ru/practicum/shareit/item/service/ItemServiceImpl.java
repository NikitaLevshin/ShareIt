package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        return itemRepository.getAllByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(int id) {
        return ItemMapper.toItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, int ownerId) {
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.fromItemDto(itemDto, ownerId), ownerId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int ownerId, int id) {
        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.fromItemDto(itemDto, ownerId), ownerId, id));
    }

    @Override
    public void delete(int id) {
        itemRepository.delete(id);
    }
}
