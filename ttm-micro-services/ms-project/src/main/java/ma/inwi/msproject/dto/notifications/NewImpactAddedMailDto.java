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
public class NewImpactAddedMailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;
    private String actionLabel;
    private Set<String> comments;
    private String actionCreatedBy;
    private String impactSenderEmail;
    private String actionDocument;
    private String requiredAction;

}
