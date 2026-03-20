package ma.inwi.msproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.TrackingType;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingDto {
    private Long id;
    private TrackingType trackingType;
    private Set<Long> trackingGateIds;
}
