package ru.sshibko.LinkoBot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private Long id;

    private String messageText;

    private String description;

    private LocalDateTime receivedAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
