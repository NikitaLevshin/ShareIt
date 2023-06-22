package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemBooking {
    private int id;
    private String name;
    private String description;
    private int userId;
    private Boolean available;
    private ItemWithBookingDto lastBooking;
    private ItemWithBookingDto nextBooking;
    private List<CommentDto> comments;
}
