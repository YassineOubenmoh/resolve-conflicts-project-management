package ma.inwi.ms_iam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ResetPassword {
    private String oldPassword;
    private String newPassword;
}
