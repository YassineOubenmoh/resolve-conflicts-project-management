package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.GateType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequiredActionGlobalResponse {
    private String requiredAction;
    private String departement;
    private GateType gateType;
    private String projectTitle;
}

