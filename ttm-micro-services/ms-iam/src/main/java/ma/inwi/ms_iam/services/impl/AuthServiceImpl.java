package ma.inwi.ms_iam.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.inwi.ms_iam.client.KeycloakClient;
import ma.inwi.ms_iam.client.ProjectClient;
import ma.inwi.ms_iam.configuration.KeycloakConfig;
import ma.inwi.ms_iam.dto.*;
import ma.inwi.ms_iam.entities.User;
import ma.inwi.ms_iam.exception.UserNotFoundException;
import ma.inwi.ms_iam.mappers.UserMapper;
import ma.inwi.ms_iam.repository.UserRepository;
import ma.inwi.ms_iam.services.AuthService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static ma.inwi.ms_iam.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final KeycloakClient keycloakClient;
    private final KeycloakConfig keycloakConfig;
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final RestTemplate restTemplate;

    private final ProjectClient projectClient;

    @Value("${authorization.realm}")
    public String realm;
    @Value("${authorization.client.id}")
    public String clientId;
    @Value("${authorization.client.secret}")
    public String clientSecret;
    @Value("${authorization.server-url}")
    private String serverUrl;

    private HttpHeaders createFormUrlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Override
    public TokenDto login(LoginDto loginDto) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, PASSWORD);
        map.add(USERNAME, loginDto.username());
        map.add(PASSWORD, loginDto.password());
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        ResponseEntity<AccessTokenResponse> accessToken = keycloakClient.login(map);
        if (Objects.isNull(accessToken)) {
            throw new RuntimeException();
        } else {
            accessToken.getBody();
        }

        return TokenDto.builder()
                .accessToken(accessToken.getBody().getToken())
                .expIn(accessToken.getBody().getExpiresIn())
                .refExpIn(accessToken.getBody().getRefreshExpiresIn())
                .refreshToken(accessToken.getBody().getRefreshToken())
                .refreshExpiresIn(accessToken.getBody().getRefreshExpiresIn())
                .tokenType(accessToken.getBody().getTokenType())
                .build();
    }

    @Override
    public void logout(RefreshTokenDTO refreshToken) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(GRANT_TYPE, REFRESH_TOKEN);
        requestParams.add(CLIENT_ID, clientId);
        requestParams.add(CLIENT_SECRET, clientSecret);
        requestParams.add(REFRESH_TOKEN, refreshToken.getRefreshToken());

        keycloakClient.logout(realm, requestParams);
    }


    @Override
    public ResponseEntity<String> resetPassword(ResetPassword request,
                                                Principal principal) {
        String username = principal.getName();


        // Authenticate with old password
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, PASSWORD);
        body.add(CLIENT_ID, clientId);
        body.add(CLIENT_SECRET, clientSecret);
        body.add(USERNAME, username);
        body.add(PASSWORD, request.getOldPassword());

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        String tokenUrl = serverUrl + REALMS + realm + PROTOCOL_OPEN_ID_CONNECT_TOKEN;

        try {
            restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
        }


        // Update password using Admin API
        List<UserRepresentation> users = keycloak.realm(realm).users().search(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String userId = users.get(0).getId();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getNewPassword());
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(userId).resetPassword(credential);
        return ResponseEntity.ok("Password changed successfully");
    }


    @Override
    public ResponseEntity<String> forgotPassword(String email) {
        try {
            String adminToken = getAdminToken();
            String userId = getUserIdByEmail(email, adminToken);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            String resetUrl = serverUrl + ADMIN + REALMS + realm + "/users/" + userId + "/execute-actions-email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(AUTHORIZATION, BEARER + adminToken);

            List<String> actions = Collections.singletonList("UPDATE_PASSWORD");
            HttpEntity<List<String>> entity = new HttpEntity<>(actions, headers);

            ResponseEntity<String> response = restTemplate.exchange(resetUrl, HttpMethod.PUT, entity, String.class);

            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending password reset email.");
        }
    }

    private String getUserIdByEmail(String email, String adminToken) {
        String usersUrl = serverUrl + ADMIN + REALMS + realm + "/users?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + adminToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(usersUrl, HttpMethod.GET, entity, List.class);

        response.getBody();
        if (!response.getBody().isEmpty()) {
            Map user = (Map) response.getBody().get(0);
            return user.get("id").toString();
        }

        return null;
    }


    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    // Retrieve roles of the user
                    List<RoleRepresentation> realmRoles = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(user.getId()) // Use user's ID to fetch their roles
                            .roles()
                            .realmLevel()
                            .listEffective();

                    // Collect role names
                    List<String> roleNames = realmRoles.stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    return UserDto.builder()
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .department(user.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames) // Add roles to UserDto
                            .build();
                })
                .toList();



        userDtos.forEach(user -> userRepository.save(userMapper.userDtoToUser(user)));
        return ResponseEntity.ok(userDtos);

    }


    @Override
    public List<UserDto> getUserByDepartment(List<String> departments) {
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        return users.stream()
                .filter(userRepresentation -> {
                    // Get realm roles for the user
                    List<RoleRepresentation> realmRoles = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles().realmLevel().listEffective();

                    // Check if the user has the "SPOC" role
                    boolean hasSpocRole = realmRoles.stream()
                            .anyMatch(role -> "SPOC".equals(role.getName()));

                    // Check if the user belongs to one of the given departments
                    String userDepartment = "";
                    if (userRepresentation.getAttributes() != null &&
                            userRepresentation.getAttributes().get(DEPARTMENT) != null) {
                        userDepartment = userRepresentation.getAttributes()
                                .get(DEPARTMENT).get(0);
                    }

                    return hasSpocRole && departments.contains(userDepartment);
                })
                .map(userRepresentation -> {
                    // Extract role names to populate in UserDto
                    List<String> roleNames = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles()
                            .realmLevel()
                            .listEffective()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    return UserDto.builder()
                            .username(userRepresentation.getUsername())
                            .firstName(userRepresentation.getFirstName())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .department(userRepresentation.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames)
                            .build();
                })
                .toList();
    }


    //Method to retrieve users by department
    @Override
    public List<UserDto> getUsersByDepartment(String department) {
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        return users.stream()
                .filter(userRepresentation -> {
                    if (userRepresentation.getAttributes() != null &&
                            userRepresentation.getAttributes().get(DEPARTMENT) != null) {
                        String userDepartment = userRepresentation.getAttributes()
                                .get(DEPARTMENT).get(0);
                        return department.equals(userDepartment);
                    }
                    return false;
                })
                .map(userRepresentation -> {
                    // Extract all role names to populate in UserDto
                    List<String> roleNames = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles()
                            .realmLevel()
                            .listEffective()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    return UserDto.builder()
                            .username(userRepresentation.getUsername())
                            .firstName(userRepresentation.getFirstName())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .department(userRepresentation.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames)
                            .build();
                })
                .toList();
    }



    @Override
    public UserDto getUserByEmail(String email) {
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .search(null, null, null, email, 0, 100);

        return users.stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst()
                .map(this::mapToUserDto)
                .orElse(null);
    }

    @Override
    public void affectProjectToInterlocutor(String username, Long projectId) {
        User user = userRepository.findUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new UserNotFoundException("No interlocutor found by username: " + username);
        }

        if (user.getProjectsId().contains(projectId)) {
            throw new RuntimeException("Project with id " + projectId + " is already assigned to user: " + username);
        }

        user.getProjectsId().add(projectId);
        userRepository.save(user);

        log.info("Project with id {} was assigned to {} interlocutor!", projectId, username);
    }


    @Override
    public UserDto getUserByUsername(String username) {
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .search(username, 0, 100);

        return users.stream()
                .filter(user -> username.equalsIgnoreCase(user.getUsername()))
                .findFirst()
                .map(this::mapToUserDto)
                .orElse(null);
    }

    private UserDto mapToUserDto(UserRepresentation userRepresentation) {
        List<String> roleNames = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .get(userRepresentation.getId())
                .roles()
                .realmLevel()
                .listEffective()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();

        String department = "";
        if (userRepresentation.getAttributes() != null &&
                userRepresentation.getAttributes().get(DEPARTMENT) != null) {
            department = userRepresentation.getAttributes().get(DEPARTMENT).get(0);
        }

        return UserDto.builder()
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .department(department)
                .roles(roleNames)
                .build();
    }


    @Override
    public List<UserDto> getUsersOfAllDepartment(List<String> departments) {
        // Retrieve the list of all users from the Keycloak realm
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        return users.stream()
                .filter(userRepresentation -> {
                    // Get the user's department
                    if (userRepresentation.getAttributes() != null &&
                            userRepresentation.getAttributes().get(DEPARTMENT) != null) {
                        String userDepartment = userRepresentation.getAttributes().get(DEPARTMENT).get(0);
                        return departments.contains(userDepartment);
                    }
                    return false;
                })
                .map(userRepresentation -> {
                    // Extract role names for each user
                    List<String> roleNames = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles()
                            .realmLevel()
                            .listEffective()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    // Return the UserDto with relevant details
                    return UserDto.builder()
                            .username(userRepresentation.getUsername())
                            .firstName(userRepresentation.getFirstName())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .department(userRepresentation.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames)
                            .build();
                })
                .toList();
    }





    @Override
    public List<String> getAllRoles() {
        return keycloakConfig.keycloak()
                .realm(realm)
                .roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();
    }


    @Override
    public Map refreshAccessToken(RefreshTokenDTO refreshToken) {
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, REFRESH_TOKEN);
        params.add(CLIENT_ID, clientId);
        params.add(CLIENT_SECRET, clientSecret);
        params.add(REFRESH_TOKEN, refreshToken.getRefreshToken());

        HttpHeaders headers = createFormUrlEncodedHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return response.getBody();
    }


    @Override
    public void signUp(UserRegistrationDTO dto) {

        String keycloakUrl = serverUrl + ADMIN + REALMS + realm + "/users";
        String adminToken = getAdminToken(); // Get the admin token

        HttpHeaders headers = createFormUrlEncodedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(adminToken); // Add the admin token to request's header

        // create the object with necessary fields
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put(USERNAME, dto.getUsername());
        userPayload.put(FIRSTNAME, dto.getFirstName());
        userPayload.put(LASTNAME, dto.getLastName());
        userPayload.put(EMAIL, dto.getEmail());
        userPayload.put(ENABLED, true);

        // Add required attributes ("department")
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(DEPARTMENT, List.of(dto.getDepartment()));

        userPayload.put("attributes", attributes);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                keycloakUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("User created successfully..");
            setUserPassword(dto.getUsername(), dto.getPassword(), adminToken);
        } else {
            throw new RuntimeException("Error creating user: " + response.getBody());
        }


        // save this new user in database

        try {



        log.info("saving user...");

        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    // Retrieve roles of the user
                    List<RoleRepresentation> realmRoles = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(user.getId()) // Use user's ID to fetch their roles
                            .roles()
                            .realmLevel()
                            .listEffective();

                    // Collect role names
                    List<String> roleNames = realmRoles.stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    return UserDto.builder()
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .department(user.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames) // Add roles to UserDto
                            .build();
                })
                .toList();


        List<User> listUsers = new ArrayList<>();

        userDtos.forEach(user -> listUsers.add(userMapper.userDtoToUser(user)));

        listUsers.forEach(user -> {
            if(Objects.isNull(userRepository.findUserByUsername(user.getUsername()))){
                userRepository.save(user);
            }
        });

        log.info("user has been saved successfully!");

        }
        catch (Exception e){
            log.error("error saving user : ", e);
        }



    }


    @Override
    public void assignRolesToUser(String username, String roleName) {
        String token = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Get user ID by username
        String userIdUrl = String.format("%s/admin/realms/%s/users?username=%s", serverUrl, realm, username);
        ResponseEntity<UserRepresentation[]> userResponse = restTemplate.exchange(
                userIdUrl, HttpMethod.GET, entity, UserRepresentation[].class
        );

        UserRepresentation[] users = userResponse.getBody();
        if (users == null || users.length == 0) {
            throw new RuntimeException("User not found: " + username);
        }

        String userId = users[0].getId();

        // Fetch role representation by name
        String roleUrl = String.format("%s/admin/realms/%s/roles/%s", serverUrl, realm, roleName);
        ResponseEntity<RoleRepresentation> roleResponse = restTemplate.exchange(
                roleUrl, HttpMethod.GET, entity, RoleRepresentation.class
        );

        RoleRepresentation role = roleResponse.getBody();
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        // Assign the role
        String assignUrl = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", serverUrl, realm, userId);
        HttpEntity<List<RoleRepresentation>> assignRequest = new HttpEntity<>(List.of(role), headers);

        restTemplate.postForEntity(assignUrl, assignRequest, Void.class);
    }

    @Override
    public List<User> getProjectsByUsername(String username) {
        return List.of();
    }


    public void setUserPassword(String username, String password, String adminToken) {

        // Get user ID from Keycloak
        String getUserUrl = serverUrl + ADMIN + REALMS + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                getUserUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        response.getBody();
        if (response.getBody().isEmpty()) {
            throw new RuntimeException("User not found in Keycloak");
        }

        // Extract user ID
        Map<String, Object> user = response.getBody().get(0);
        String userId = (String) user.get("id");

        // Set password
        String passwordUrl = serverUrl + ADMIN + REALMS + realm + "/users/" + userId + "/reset-password";

        Map<String, Object> passwordPayload = new HashMap<>();
        passwordPayload.put("type", PASSWORD);
        passwordPayload.put("value", password);
        passwordPayload.put("temporary", false);

        HttpEntity<Map<String, Object>> passwordEntity = new HttpEntity<>(passwordPayload, headers);

        restTemplate.exchange(passwordUrl, HttpMethod.PUT, passwordEntity, String.class);

        log.info("Password set successfully for user: {}", username);

    }


    public String getAdminToken() {

        String keycloakUrl = serverUrl + REALMS + realm + PROTOCOL_OPEN_ID_CONNECT_TOKEN;

        // Define request parameters
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, CLIENT_CREDENTIALS);
        body.add(CLIENT_ID, clientId);
        body.add(CLIENT_SECRET, clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                keycloakUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Extract access token
        return response.getBody().get(ACCESS_TOKEN).toString();
    }


    public List<User> getInterlocutorsByDepartment(String department) {
        List<User> users = userRepository.findInterlocutorsByDepartment(department);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No user is found !");
        }
        return users;
    }


    public Set<InterlocutorDto> getInterlocutorsData(String department) {
        List<User> interlocutors = getInterlocutorsByDepartment(department);

        Set<User> interlocutorsSignaling = interlocutors.stream()
                .filter(i -> i.getRoles().contains("INTERLOCUTEUR_SIGNALE_IMPACT"))
                .collect(Collectors.toSet());

        Set<User> interlocutorsResponding = interlocutors.stream()
                .filter(i -> i.getRoles().contains("INTERLOCUTEUR_RETOUR_IMPACT"))
                .collect(Collectors.toSet());

        Set<InterlocutorDto> result = new HashSet<>();

        for (User signalingUser : interlocutorsSignaling) {
            Set<Long> signalingProjects = new HashSet<>(signalingUser.getProjectsId());

            for (User respondingUser : interlocutorsResponding) {
                Set<Long> respondingProjects = new HashSet<>(respondingUser.getProjectsId());

                // Find intersection of project IDs
                Set<Long> commonProjects = new HashSet<>(signalingProjects);
                commonProjects.retainAll(respondingProjects);

                for (Long projectId : commonProjects) {
                    ResponseEntity<ProjectDto> response = projectClient.getProjectById(projectId);
                    if (response == null || !response.getStatusCode().is2xxSuccessful()) continue;

                    ProjectDto projectDto = response.getBody();
                    if (projectDto == null) continue;

                    InterlocutorDto dto = new InterlocutorDto();
                    dto.setInterlocutorSignalingId(signalingUser.getId());
                    dto.setInterlocutorRespondingId(respondingUser.getId());
                    dto.setInterlocutorSignalingFirstName(signalingUser.getFirstName());
                    dto.setInterlocutorSignalingLastName(signalingUser.getLastName());
                    dto.setInterlocutorRespondingFirstName(respondingUser.getFirstName());
                    dto.setInterlocutorRespondingLastName(respondingUser.getLastName());
                    dto.setProjectName(projectDto.getTitle());
                    dto.setProjectId(projectId);

                    result.add(dto);
                }
            }
        }

        return result;
    }



    @Override
    public UserDto getSpocForDepartment(String department) {
        // Retrieve the list of all users from the Keycloak realm
        List<UserRepresentation> users = keycloakConfig.keycloak()
                .realms()
                .realm(realm)
                .users()
                .list();

        return users.stream()
                .filter(userRepresentation -> {
                    // Check if user belongs to the given department
                    if (userRepresentation.getAttributes() != null &&
                            userRepresentation.getAttributes().get(DEPARTMENT) != null) {
                        String userDepartment = userRepresentation.getAttributes().get(DEPARTMENT).get(0);
                        return department.equals(userDepartment);
                    }
                    return false;
                })
                .filter(userRepresentation -> {
                    // Check if user has the role 'SPOC'
                    List<String> roleNames = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles()
                            .realmLevel()
                            .listEffective()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();
                    return roleNames.contains("SPOC");
                })
                .findFirst()
                .map(userRepresentation -> {
                    // Map to UserDto
                    List<String> roleNames = keycloakConfig.keycloak()
                            .realms()
                            .realm(realm)
                            .users()
                            .get(userRepresentation.getId())
                            .roles()
                            .realmLevel()
                            .listEffective()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    return UserDto.builder()
                            .username(userRepresentation.getUsername())
                            .firstName(userRepresentation.getFirstName())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .department(userRepresentation.getAttributes().get(DEPARTMENT).get(0))
                            .roles(roleNames)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("No SPOC found for department: " + department));
    }




    public List<Long> getProjectsReservedForSpoc(String department){
        UserDto userDto = getSpocForDepartment(department);
        User user = userRepository.findUserByUsername(userDto.getUsername());
        return user.getProjectsId();
    }


    @Transactional
    public void deleteAffectationProjectId(Long projectId) {
        List<User> users = userRepository.findInterlocutorsOfProject(projectId);
        if (users.isEmpty()){
            throw new UserNotFoundException("No interlocutor was found !");
        }

        for (User user : users) {
            if (user.getProjectsId() != null && user.getProjectsId().contains(projectId)) {
                user.getProjectsId().remove(projectId);
            }
        }

        userRepository.saveAll(users);
    }


    public User keepProjectForSpoc(String username, Long projectId) {
        User user = userRepository.findUserByUsername(username);
        List<Long> existingProjectIds = user.getProjectsId();
        if (!existingProjectIds.contains(projectId)) {
            existingProjectIds.add(projectId);
        }
        userRepository.save(user);
        return user;
    }




    // Projects Of Interlocutors
    public List<Long> getProjectsOfInterlocutor(String username){
        User user = userRepository.findUserByUsername(username);
        if (user == null){
            throw new UserNotFoundException("User not found !");
        }
        return user.getProjectsId();
    }











}
