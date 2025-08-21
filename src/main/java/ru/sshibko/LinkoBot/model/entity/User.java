package ru.sshibko.LinkoBot.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    private Long ChatId;

    private String firstName;

    private String lastName;

    private String userName;

    private int userAge;

    private LocalDateTime registeredAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Message> messageList;
}
