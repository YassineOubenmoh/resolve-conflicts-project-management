package ma.inwi.ms_notif.dto;

import lombok.*;
import ma.inwi.ms_notif.enums.GateType;

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
