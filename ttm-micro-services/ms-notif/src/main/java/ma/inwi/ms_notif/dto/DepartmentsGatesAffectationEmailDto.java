package ma.inwi.ms_notif.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentsGatesAffectationEmailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;

    private String emailSender;

    private DepartmentGateRequiredAction departmentGateRequiredActions;
}
