package ru.sshibko.LinkoBot.model.repository;

import org.springframework.data.jpa.repository.Query;
import ru.sshibko.LinkoBot.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    String query1 = "SELECT * FROM message  WHERE "
            + "description LIKE %:keyword%"
            + " OR link LIKE %:keyword%";

    String query33 = "SELECT * FROM message WHERE link LIKE %:keyword%";
    String query34 = "SELECT * FROM message WHERE link LIKE %?1%";
    @Query(value = query1, nativeQuery = true)
    public List<Message> search(String keyword);
}
