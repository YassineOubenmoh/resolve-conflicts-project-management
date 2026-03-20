package ma.inwi.msproject.dto.frontdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.entities.RequiredAction;
import ma.inwi.msproject.enums.GateType;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GateProjectFront {
    private Long projectId;
    private String titleProject;
    private GateType gate;
    private boolean currentGate;
    private Set<String> requiredActions;
}
