package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(int id, int userId) {
        log.info("Запрос на получение бронирования с id {}", id);
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException("Бронирование не найдено"));
        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwner().getId()) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Вы не можете смотреть данное бронирование");
        }
    }

    @Override
    public BookingDto create(BookingDto bookingDto, int userId) {
        log.info("Запрос на создание бронирования пользователем с id {}", userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Время конца бронирования не может быть раньше времени старта");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Время начала и конца бронирования не могут совпадать");
        }
        User user = UserMapper.fromUserDto(userService.getById(userId));
        Item item = itemService.getById(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId() == userId) {
            throw new BookingNotFoundException("Нельзя забронировать собственную вещь");
        }
        Booking booking = BookingMapper.fromBookingDto(bookingDto, item, user);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(int id, int userId, boolean approve) {
        log.info("Запрос на подтверждение бронирования с id {}", id);
        Booking booking = bookingRepository.getReferenceById(id);
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Статус бронирования должен быть WAITING");
        }
        if (userId != booking.getItem().getOwner().getId()) {
            throw new BookingNotFoundException("Вы не владелец брони");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByUserId(int userId, String state) {
        log.info("Запрос на получение запросов бронирования пользователя с id {}", userId);
        userService.getById(userId);
        if (state == null) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
        }
        switch (state) {
            case "WAITING":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            case "FUTURE":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case "CURRENT":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId, LocalDateTime.now(), LocalDateTime.now()));
            case "ALL":
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByOwnerId(int userId, String state) {
        log.info("Запрос на получение запросов бронирования владельцем с id {}", userId);
        userService.getById(userId);
        if (state == null) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId));
        }
        switch (state) {
            case "WAITING":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            case "FUTURE":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case "CURRENT":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now()));
            case "ALL":
                return BookingMapper.toDtoList(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId));
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
