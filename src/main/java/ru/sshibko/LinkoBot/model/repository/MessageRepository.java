package ru.sshibko.LinkoBot.model.repository;

import ru.sshibko.LinkoBot.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
