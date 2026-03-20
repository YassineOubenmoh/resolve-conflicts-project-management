package ma.inwi.msproject.dto.notifications;

import lombok.*;
import ma.inwi.msproject.dto.UserDetails;
import ma.inwi.msproject.enums.GateType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuspensionGateMailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;
    private boolean inProgress;
    private String emailOwner;

    private GateType gate;
}