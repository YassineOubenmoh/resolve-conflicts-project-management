package ma.inwi.ms_notif.dto;

import lombok.*;
import ma.inwi.ms_notif.enums.ValidationStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewResponseToImpactMailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;
    private String responseToActionLabel;
    private ValidationStatus validationStatus;
    private String justificationStatus;
    private String validatedBy;
    private String responseEmailSender;
    private String responseDocument;
}
