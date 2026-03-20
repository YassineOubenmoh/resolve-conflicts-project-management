package ma.inwi.msproject.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.TrackingType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PieMarketTypeDto {
    private String marketType;
    private double marketTypePercentage;
}
