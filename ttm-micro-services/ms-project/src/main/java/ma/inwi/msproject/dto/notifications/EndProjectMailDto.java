package ma.inwi.msproject.dto.notifications;

import lombok.*;
import ma.inwi.msproject.dto.UserDetails;
import ma.inwi.msproject.enums.GateType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndProjectMailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;
    private GateType futureGate;
    private GateType passedGate;
    private Set<String> information;
    private Set<String> actions;
    private Set<String> decisions;
    private String passingDate;

    private String emailSender;

    private Long daysTtm;
}
