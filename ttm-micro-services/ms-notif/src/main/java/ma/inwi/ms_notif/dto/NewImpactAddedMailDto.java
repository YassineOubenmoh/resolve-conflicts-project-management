package ma.inwi.ms_notif.dto;

import lombok.*;

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