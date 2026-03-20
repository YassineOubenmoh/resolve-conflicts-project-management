package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.DecisionType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GateProjectDto {
    private Long id;
    private Long trackingGateId;
    private boolean currentGate;
    private LocalDateTime passingDate;
    private boolean inProgress = true;
    private boolean startingGate;
    private DecisionType decisionType;
    private Set<String> information;
    private Set<String> actions;
    private Set<String> decisions;
    private Long projectId;
    private Set<Long> departementGateProjectIds;
    private boolean projectCompleted;
}

