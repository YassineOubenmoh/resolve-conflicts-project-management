package ma.inwi.msproject.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistogramImpactDto {
    public String impactFeedback;
    public double impactFeedbackPercentage;
}
