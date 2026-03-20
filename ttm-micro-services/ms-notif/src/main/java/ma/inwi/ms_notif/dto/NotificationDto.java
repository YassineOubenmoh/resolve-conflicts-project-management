package ma.inwi.ms_notif.dto;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String senderMail;
    private String receiverMail;
    private String content;
    private boolean consumed;
    private LocalDateTime createdAt;
}
