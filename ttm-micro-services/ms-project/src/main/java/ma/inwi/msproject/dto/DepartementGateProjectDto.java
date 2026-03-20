package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartementGateProjectDto {
    private Long id;
    private Long gateProjectId;
    private Long departementId;
    private Set<String> requiredActionLabels;
}
