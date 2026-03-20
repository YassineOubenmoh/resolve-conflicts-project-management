package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.enums.StatusProjectAssignement;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectFrontDto {

    private Long id;

    private String ownerUsername;

    private String ownerFullName;

    private String title;
    private String description;
    private String marketType;
    private String projectType;
    private String ttmComitteeSubCategory;
    private String subcategoryCommercialCodir;
    private Boolean isConfidential;
    private Date dateStartTtm;
    private String expressionOfNeed;
    private String briefCommunication;
    private String briefCDG;
    private String regulatoryBrief;
    private Set<String> attachedDocuments;
    private Set<String> comments;
    private LocalDateTime dateCreation;
    private Set<String> moas;
    private Long trackingId;
    private Set<Long> gateProjectIds;
    private GateType currentGate;
    private Set<String> requiredActions;
    private List<String> departments;

    private StatusProjectAssignement assignedToInterlocutors;
}
