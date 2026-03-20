package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.ValidationStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto {
    private Long id;
    private String actionLabel;
    private String responseToActionLabel;
    private Set<String> comments;
    private String actionCreatedBy;
    private String impactSenderEmail;
    private String actionDocument;
    private String responseDocument;
    private ValidationStatus validationStatus;
    private String justificationStatus;
    private String validatedBy;
    private String responseEmailSender;
    private Long requiredActionId;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}

