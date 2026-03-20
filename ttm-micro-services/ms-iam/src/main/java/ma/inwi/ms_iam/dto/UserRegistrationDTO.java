package ma.inwi.ms_iam.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor(force = true)
@Builder
public class UserRegistrationDTO {
    private String firstName;
    private String lastName;
    private String username;

    @NonNull
    private String department;

    @NonNull
    private String email;
    private String password;
    private List<String> roles;


}
