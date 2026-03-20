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
public class PieProjectTypeDto {
    private String projectType;
    private double projectTypePercentage;
}
