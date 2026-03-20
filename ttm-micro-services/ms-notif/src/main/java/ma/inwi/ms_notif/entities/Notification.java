package ma.inwi.ms_notif.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderMail;
    private String receiverMail;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean consumed;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
