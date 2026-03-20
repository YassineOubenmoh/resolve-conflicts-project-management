package ma.inwi.ms_notif.repositories;

import ma.inwi.ms_notif.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.consumed = false AND n.senderMail = :senderEmail")
    List<Notification> findUnconsumedMessagesBySenderMail(@Param("senderEmail") String senderEmail);

    @Query("SELECT n FROM Notification n WHERE n.consumed = false AND n.receiverMail = :receiverEmail")
    List<Notification> findUnconsumedMessagesByReceiverMail(@Param("receiverEmail") String receiverEmail);
}

