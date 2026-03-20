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
public class TrackingGateDto {
    private Long id;
    private Long gateId;
    private Long trackingId;
    private Long gateBeforeId;
    private Long gateAfterId;
    private Set<Long> gateProjectIds;
}
