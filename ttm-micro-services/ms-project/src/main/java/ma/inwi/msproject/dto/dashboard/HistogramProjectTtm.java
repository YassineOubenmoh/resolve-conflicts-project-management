package ma.inwi.msproject.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistogramProjectTtm {
    public String projectTitle;
    public double ttm;
}
