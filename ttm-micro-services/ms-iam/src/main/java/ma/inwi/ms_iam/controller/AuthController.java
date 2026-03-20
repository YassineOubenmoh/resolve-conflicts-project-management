package ma.inwi.ms_iam.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.inwi.ms_iam.dto.*;
import ma.inwi.ms_iam.entities.User;
import ma.inwi.ms_iam.exception.UserNotFoundException;
import ma.inwi.ms_iam.mappers.UserMapper;
import ma.inwi.ms_iam.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;


    @PostMapping("/auth/login")
    public TokenDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }



    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenDTO refreshToken) {
        authService.logout(refreshToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/auth/signup")
    public ResponseEntity<String> signUp(@RequestBody UserRegistrationDTO signUpRequest) {
        try {
            authService.signUp(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
        }
    }


    @PostMapping("/auth/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassword request,
                                                    Principal principal) {
        return authService.resetPassword(request, principal);
    }


    @PostMapping("/auth/password/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }



    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO refreshToken) {
        try {
            Map<String, Object> response = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
    }


    @GetMapping("/users")
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return authService.getAllUsers();
    }


    @PostMapping("/users/by-departments")
    public List<UserDto> getUsersByDepartments(@RequestBody List<String> departments) {
        return authService.getUserByDepartment(departments);
    }


    @GetMapping("/users/roles")
    @PreAuthorize(value = "hasAuthority('USER')")
    public List<String> getAllRoles() {
        return authService.getAllRoles();
    }


    @PostMapping("/users/roles/assign")
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<String> assignRolesToUser(@RequestBody RoleAssignmentRequest request) {
        authService.assignRolesToUser(request.getUsername(), request.getRoleName());
        return ResponseEntity.ok("Roles assigned successfully");
    }


    @GetMapping("/users/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        UserDto user = authService.getUserByEmail(email);
        if (!Objects.isNull(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/by-username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        UserDto user = authService.getUserByUsername(username);
        if (!Objects.isNull(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/affectProject")
    public ResponseEntity<String> affectProjectToInterlocutor(@RequestParam String username, @RequestParam Long projectId) {


        try {
            authService.affectProjectToInterlocutor(username, projectId);
            return ResponseEntity.status(HttpStatus.CREATED).body("project with id " + projectId + " was affected to "
                    + username + " interlocutor! ");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error affecting project : " + e.getMessage());
        }


    }

    @PostMapping("/users-all-deps")
    public ResponseEntity<List<UserDto>> getUsersOfAllDepartments(@RequestBody List<String> departments) {
        List<UserDto> users = authService.getUsersOfAllDepartment(departments);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/interlocutors-dep")
    public ResponseEntity<Set<UserDtoRs>> getInterlocutorsByDepartment(@RequestParam("department") String department){
        List<User> userDtoRs = authService.getInterlocutorsByDepartment(department);
        Set<UserDtoRs> usersDtos = userDtoRs.stream()
                .map(userMapper::userToUserDtoRs)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(usersDtos);
    }

    @GetMapping("/interlocutors-affectations")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<InterlocutorDto>> getInterlocutorsData(@RequestParam("department") String department){
        Set<InterlocutorDto> interlocutors = authService.getInterlocutorsData(department);
        return ResponseEntity.ok(interlocutors);
    }

    //export interlocutors
    @GetMapping("/interlocutors-affectations/export/{department}")
    @PreAuthorize("hasAuthority('SPOC')")
    public void exportInterlocutorsToCSV(@PathVariable String department, HttpServletResponse response) throws IOException {
        Set<InterlocutorDto> interlocutors = authService.getInterlocutorsData(department);

        if (interlocutors == null || interlocutors.isEmpty()) {
            throw new UserNotFoundException("No user was found!");
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=interlocutors.csv");

        PrintWriter writer = response.getWriter();

        // CSV Header matching InterlocutorDto fields
        writer.println("Interlocutor Signaling ID,Interlocutor Responding ID,Signaling First Name,Responding First Name,Signaling Last Name,Responding Last Name,Project Name,Project ID");

        for (InterlocutorDto interlocutor : interlocutors) {
            writer.println(String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s",
                    interlocutor.getInterlocutorSignalingId() != null ? interlocutor.getInterlocutorSignalingId() : "",
                    interlocutor.getInterlocutorRespondingId() != null ? interlocutor.getInterlocutorRespondingId() : "",
                    escape(interlocutor.getInterlocutorSignalingFirstName()),
                    escape(interlocutor.getInterlocutorRespondingFirstName()),
                    escape(interlocutor.getInterlocutorSignalingLastName()),
                    escape(interlocutor.getInterlocutorRespondingLastName()),
                    escape(interlocutor.getProjectName()),
                    interlocutor.getProjectId() != null ? interlocutor.getProjectId() : ""
            ));
        }

        writer.flush();
        writer.close();
    }

    // Escape method as before
    private String escape(String input) {
        if (input == null) return "";
        boolean hasSpecialChar = input.contains(",") || input.contains("\"") || input.contains("\n");
        String escaped = input.replace("\"", "\"\"");
        return hasSpecialChar ? "\"" + escaped + "\"" : escaped;
    }



    @PutMapping("/remove-affectation/{projectId}")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<String> deleteAffectationProjectId(@PathVariable("projectId") Long projectId) {
        authService.deleteAffectationProjectId(projectId);
        return ResponseEntity.ok("Project ID " + projectId + " has been removed from affected users.");
    }


    @PutMapping("/spoc-keep")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<User> keepProjectForSpoc(
            @RequestParam("username") String username,
            @RequestParam("projectId") Long projectId){
        User user = authService.keepProjectForSpoc(username, projectId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/reserved-for-spoc/{department}")
    public ResponseEntity<List<Long>> getProjectsReservedForSpoc(
            @PathVariable("department") String department){
        List<Long> spocProjectIds = authService.getProjectsReservedForSpoc(department);
        return new ResponseEntity<>(spocProjectIds, HttpStatus.OK);
    }


    @GetMapping("/users-by-department/{department}")
    public ResponseEntity<List<UserDto>> getUsersByDepartment(@PathVariable("department") String department){
        List<UserDto> users = authService.getUsersByDepartment(department);
        if (users.isEmpty()){
            throw new UserNotFoundException("No user was found !");
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/interlocutor-projects/{username}")
    public ResponseEntity<List<Long>> getProjectsOfInterlocutor(@PathVariable("username") String username){
        List<Long> interlocutorProjects = authService.getProjectsOfInterlocutor(username);
        return new ResponseEntity<>(interlocutorProjects, HttpStatus.OK);
    }








}
