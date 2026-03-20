package ma.inwi.ms_iam.dto;


import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDtoRq {

    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String email;
    private List<Long> projectsId;
    private List<String> roles;



}
