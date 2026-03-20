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
public class ConfigRequiredActionDto {

    private Long id;
    private Long departementId;
    private Long gateId;
    private Set<String> requiredActions;
}

