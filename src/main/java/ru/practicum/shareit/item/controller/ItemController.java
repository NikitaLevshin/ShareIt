package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader ("X-Sharer-User-Id") int ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader ("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader ("X-Sharer-User-Id") int ownerId,
                          @Valid @RequestBody ItemDto itemDto,
                          @PathVariable int id) {
        return  itemService.update(itemDto, ownerId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        itemService.delete(id);
    }
}
