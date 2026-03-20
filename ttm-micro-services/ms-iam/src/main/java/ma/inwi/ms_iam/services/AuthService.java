package ma.inwi.ms_iam.services;

import ma.inwi.ms_iam.dto.*;
import ma.inwi.ms_iam.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface AuthService {

    TokenDto login(LoginDto loginDto);

    void logout(RefreshTokenDTO refreshToken);

    ResponseEntity<String> resetPassword(ResetPassword request,
                                         Principal principal);
    ResponseEntity<String> forgotPassword( String email);

    ResponseEntity<List<UserDto>> getAllUsers();

    List<UserDto> getUserByDepartment(List<String> departments);

    UserDto getUserByEmail(String email);

    void affectProjectToInterlocutor(String username, Long projectId);

    UserDto getUserByUsername(String username);

    List<UserDto> getUsersOfAllDepartment(List<String> departments);

    List<String> getAllRoles();

    Map<String, Object> refreshAccessToken(RefreshTokenDTO refreshToken);

    void signUp(UserRegistrationDTO dto);

    void assignRolesToUser(String username, String roleName);

    List<User> getProjectsByUsername(String username);

    List<User> getInterlocutorsByDepartment(String department);

    Set<InterlocutorDto> getInterlocutorsData(String department);

    void deleteAffectationProjectId(Long projectId);

    User keepProjectForSpoc(String department, Long projectId);

    List<UserDto> getUsersByDepartment(String department);

    List<Long> getProjectsReservedForSpoc(String department);

    UserDto getSpocForDepartment(String department);

    List<Long> getProjectsOfInterlocutor(String username);
}