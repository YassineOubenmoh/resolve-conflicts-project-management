package ma.inwi.msproject.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoRs {
    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String email;
    private List<Long> projectsId;
    private List<String> roles;
}
