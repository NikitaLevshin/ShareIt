package ru.practicum.shareit.request.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequester(User requester);

    List<ItemRequest> findAllByRequesterIdIsNot(Integer requesterId, Pageable pageable);
}
