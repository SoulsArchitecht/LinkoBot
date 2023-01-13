package ru.sshibko.LinkoBot.model.repository;

import org.springframework.data.jpa.repository.Query;
import ru.sshibko.LinkoBot.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    String query0 = "SELECT * FROM message WHERE "
            + "user_id =:chatId LIMIT 10";

    String query1 = "SELECT * FROM message  WHERE "
            + "description LIKE %:keyword%"
            + " OR link LIKE %:keyword% LIMIT 10";

    String query3 = "SELECT * FROM message WHERE link LIKE %:keyword%";
    String query4 = "SELECT * FROM message WHERE link LIKE %?1%";
    @Query(value = query1, nativeQuery = true)
    List<Message> search(String keyword);

    @Query(value = query0, nativeQuery = true)
    List<Message> findAllChatIdMessages(Long chatId);
}
