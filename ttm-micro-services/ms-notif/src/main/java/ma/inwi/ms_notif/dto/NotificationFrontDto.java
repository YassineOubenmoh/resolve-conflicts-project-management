package ma.inwi.ms_notif.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationFrontDto {
    private Long id;
    private String senderMail;
    private String receiverMail;
    private String content;
    private String titleNotification;
    private boolean consumed;
    private LocalDateTime createdAt;
}
