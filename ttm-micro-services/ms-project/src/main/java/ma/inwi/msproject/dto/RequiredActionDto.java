package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequiredActionDto {
    private Long id;
    private String requiredAction;
    private Long departementGateProjectId;
}

