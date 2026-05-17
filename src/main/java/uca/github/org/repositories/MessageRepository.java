package uca.github.org.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uca.github.org.models.Message;
import uca.github.org.models.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderOrRecipientOrderBySentAtDesc(User sender, User recipient);

    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderBySentAtAsc(
            User sender, User recipient, User recipient2, User sender2);

    @Query("SELECT DISTINCT m.recipient FROM Message m WHERE m.sender.id = :userId")
    List<User> findRecipientsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.recipient.id = :userId")
    List<User> findSendersByUserId(@Param("userId") Long userId);

    long countByRecipientAndIsReadFalse(User recipient);
}