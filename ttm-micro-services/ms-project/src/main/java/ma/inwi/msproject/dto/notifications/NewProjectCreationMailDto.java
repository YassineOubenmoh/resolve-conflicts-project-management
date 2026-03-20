package ma.inwi.msproject.dto.notifications;

import lombok.*;
import ma.inwi.msproject.dto.UserDetails;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewProjectCreationMailDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;

    private String ownerEmail;
    private String ownerName;
    private String description;
    private String marketType;
    private String projectType;
    private String ttmComitteeSubCategory;
    private String subcategoryCommercialCodir;
    private String confidential;
    private String dateStartTtm;
}
