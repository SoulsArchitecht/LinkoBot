package ru.sshibko.LinkoBot.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String link;

    private String description;

    private LocalDateTime receivedAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
