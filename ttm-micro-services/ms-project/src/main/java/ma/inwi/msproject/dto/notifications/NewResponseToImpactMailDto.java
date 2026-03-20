package ma.inwi.msproject.dto.notifications;

import lombok.*;
import ma.inwi.msproject.dto.UserDetails;
import ma.inwi.msproject.enums.ValidationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

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
