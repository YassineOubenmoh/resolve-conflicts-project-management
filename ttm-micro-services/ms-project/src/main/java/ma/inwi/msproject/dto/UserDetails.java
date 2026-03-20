package ma.inwi.msproject.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String projectName;
    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String email;

}

