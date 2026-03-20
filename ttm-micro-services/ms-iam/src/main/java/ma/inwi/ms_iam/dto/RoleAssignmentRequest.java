package ma.inwi.ms_iam.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleAssignmentRequest {
    private String username;
    private String roleName;
}