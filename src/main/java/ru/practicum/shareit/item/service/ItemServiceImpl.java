package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ItemWithBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotNullException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBooking;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemBooking> getAllByOwner(int ownerId) {
        log.info("Запрос всех вещей пользователя {}", ownerId);
        userService.getById(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> getItem(item.getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(int id) {
        if (itemRepository.findById(id) == null) {
            throw new UserNotFoundException(String.format("Вещь с id %d не найдена", id));
        } else return itemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        else
            return itemRepository.findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, int ownerId) {
        log.info("Запрос на создание вещи");
        if (itemDto.getName().isBlank() || itemDto.getName() == null
                || itemDto.getDescription() == null) {
            throw new NotNullException("Поля имя, описание и доступность не могут быть пустыми");
        }
        if (itemDto.getAvailable() == null) {
            throw new NotNullException("Поле available не может быть пустым");
        }
        User user = UserMapper.fromUserDto(userService.getById(ownerId));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.fromItemDto(itemDto, user)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int ownerId, int id) {
        log.info("Запрос на обновление вещи с id {}", id);
        Item item = itemRepository.getReferenceById(id);
        if (item.getOwner().getId() != ownerId) {
            throw new UserNotFoundException("Вещь может обновить только владелец");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBooking getItem(int itemId, int userId) {
        log.info("Запрос вещи с id {}", itemId);
        try {
            Item item = itemRepository.findById(itemId);
            ItemWithBookingDto last = null;
            ItemWithBookingDto next = null;
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            if (item.getOwner().getId() == userId) {
                last = BookingMapper.toItemWithBookingDto(bookingRepository
                        .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now(), Status.APPROVED));
                next = BookingMapper.toItemWithBookingDto(bookingRepository
                        .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), Status.APPROVED));
            }
            return ItemMapper.toItemBookingDto(
                    item,
                    last,
                    next,
                    comments.stream().map(CommentMapper::toDto).collect(Collectors.toList())
            );
        } catch (NullPointerException e) {
            throw new UserNotFoundException("Вещи с таким id не существует");
        }
    }

    @Override
    public CommentDto addComment(int itemId, CommentDto commentDto, int authorId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(
                itemId,
                authorId,
                LocalDateTime.now()
        );

        if (booking == null) {
            throw new ValidationException("Вещь должна быть забронирована");
        }
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пуст");
        }
        Comment comment = CommentMapper.fromCommentDto(commentDto, booking.getItem(), booking.getBooker());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void delete(int id) {
        log.info("Запрос на удаление вещи с id {}", id);
        itemRepository.deleteById(id);
    }
}
