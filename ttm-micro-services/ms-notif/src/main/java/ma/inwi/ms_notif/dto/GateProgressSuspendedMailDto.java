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
public class GateProgressSuspendedMailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDetails userDetails;
    private GateType gateType;
    private String senderEmail;
}