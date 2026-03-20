package ma.inwi.msproject.dto.notifications;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentGateRequiredAction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String gate;
    private Set<String> requiredActions;
}
