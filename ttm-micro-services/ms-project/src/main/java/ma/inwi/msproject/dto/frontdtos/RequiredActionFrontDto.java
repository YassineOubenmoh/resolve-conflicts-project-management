package ma.inwi.msproject.dto.frontdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequiredActionFrontDto {
    private Long id;
    private String requiredAction;
    private String department;
}
