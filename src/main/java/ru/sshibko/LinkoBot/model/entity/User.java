package ru.sshibko.LinkoBot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
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
}
