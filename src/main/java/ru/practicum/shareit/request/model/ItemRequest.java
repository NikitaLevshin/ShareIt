package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int id;
    @Column
    private String description;

    @OneToOne
    @JoinColumn(name = "requester")
    private User requester;
    @FutureOrPresent
    private LocalDateTime created;
    @Transient
    private List<Item> items;
}
