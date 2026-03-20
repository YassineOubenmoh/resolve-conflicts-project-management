package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.DecisionType;
import ma.inwi.msproject.enums.GateType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GateProjectResponseDto {
    private Long id;
    private GateType gate;
    private boolean currentGate;
    private LocalDateTime passingDate;
    private boolean inProgress;
    private boolean startingGate;
    private DecisionType decisionType;
    private Set<String> information;
    private Set<String> actions;
    private Set<String> decisions;
    private Long projectId;
    private Set<Long> departementGateProjectIds;
    private boolean projectCompleted;
}
