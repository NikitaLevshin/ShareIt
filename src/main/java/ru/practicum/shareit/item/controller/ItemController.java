package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemBookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItem(@PathVariable Integer itemId,
                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable int id) {
        return itemService.update(itemDto, userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Integer authorId) {
        return itemService.addComment(itemId, commentDto, authorId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        itemService.delete(id);
    }
}
