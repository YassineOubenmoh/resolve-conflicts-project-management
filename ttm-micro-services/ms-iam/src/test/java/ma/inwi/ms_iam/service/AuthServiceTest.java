package ma.inwi.ms_iam.service;

import lombok.extern.slf4j.Slf4j;
import ma.inwi.ms_iam.client.KeycloakClient;
import ma.inwi.ms_iam.configuration.KeycloakConfig;
import ma.inwi.ms_iam.dto.*;
import ma.inwi.ms_iam.entities.User;
import ma.inwi.ms_iam.exception.SignUpBadRequestException;
import ma.inwi.ms_iam.exception.UserNotFoundException;
import ma.inwi.ms_iam.repository.UserRepository;
import ma.inwi.ms_iam.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;

import static ma.inwi.ms_iam.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


// --------------------- Extended Interface for Role Lookup ---------------------
interface ExtendedRoleScopeResource extends org.keycloak.admin.client.resource.RoleScopeResource {
    List<RoleRepresentation> listEffective();
}

@Slf4j
class AuthServiceTest {

    @Mock
    private KeycloakClient keycloakClient;

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakConfig keycloakConfig;
    @Mock
    private RealmsResource realmsResource;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the property values that are normally injected via @Value
        ReflectionTestUtils.setField(authService, REALM, "testRealm");
        ReflectionTestUtils.setField(authService, "clientId", "testClientId");
        ReflectionTestUtils.setField(authService, "clientSecret", "testClientSecret");
        ReflectionTestUtils.setField(authService, SERVER_URL, "https://test-url.com/");

        // For the forgotPassword chain: keycloak.realm().users()
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        // For the getAllUsers chain: keycloakConfig.keycloak()...
        when(keycloakConfig.keycloak()).thenReturn(keycloak);
        when(keycloak.realms()).thenReturn(realmsResource);
        when(realmsResource.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }


    // ---- Tests for Login(loginDto) ----
    @Test
    void testLogin_Success() {
        // Arrange
        LoginDto loginDto = new LoginDto("username", "password");
        AccessTokenResponse mockTokenResponse = mock(AccessTokenResponse.class);
        when(mockTokenResponse.getToken()).thenReturn("mockAccessToken");
        when(mockTokenResponse.getExpiresIn()).thenReturn(3600L);
        when(mockTokenResponse.getRefreshToken()).thenReturn("mockRefreshToken");
        when(mockTokenResponse.getRefreshExpiresIn()).thenReturn(3600L);
        when(mockTokenResponse.getTokenType()).thenReturn("Bearer");

        ResponseEntity<AccessTokenResponse> responseEntity = ResponseEntity.ok(mockTokenResponse);
        when(keycloakClient.login(any())).thenReturn(responseEntity);

        // Act
        TokenDto tokenDto = authService.login(loginDto);

        // Assert
        assertNotNull(tokenDto);
        assertEquals("mockAccessToken", tokenDto.getAccessToken());
        assertEquals(3600, tokenDto.getExpIn());
        assertEquals("mockRefreshToken", tokenDto.getRefreshToken());
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        LoginDto loginDto = new LoginDto(USERNAME, PASSWORD);
        when(keycloakClient.login(any())).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }


    // ---- Tests for resetPassword() ----
    @Test
    void testResetPassword_Failed_Authentication() {
        ResetPassword resetPassword = new ResetPassword("oldPass", "newPass");
        Principal principal = () -> "testUser";

        // Create headers and prepare the body as in the service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info("Headers: {}", headers);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, PASSWORD);
        body.add(CLIENT_ID, "testClientId");
        body.add(CLIENT_SECRET, "testClientSecret");
        body.add(USERNAME, "testUser");
        body.add(PASSWORD, resetPassword.getOldPassword());

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        // Simulate an authentication failure
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        ResponseEntity<String> response = authService.resetPassword(resetPassword, principal);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Old password is incorrect", response.getBody());
    }

    @Test
    void testResetPassword_Successful_Authentication() {
        ResetPassword resetPassword = new ResetPassword("oldPass", "newPass");
        Principal principal = () -> "testUser";

        // Prepare the authentication part as in the service implementation
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, PASSWORD);
        body.add(CLIENT_ID, "testClientId");
        body.add(CLIENT_SECRET, "testClientSecret");
        body.add(USERNAME, "testUser");
        body.add(PASSWORD, resetPassword.getOldPassword());
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        // Simulate successful authentication by returning OK
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));

        // Simulate Keycloak user retrieval
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("mockUserId");
        when(usersResource.search("testUser")).thenReturn(Collections.singletonList(userRepresentation));

        // Simulate the password reset call using the Admin API
        when(usersResource.get("mockUserId")).thenReturn(userResource);
        doNothing().when(userResource).resetPassword(any(CredentialRepresentation.class));

        ResponseEntity<String> response = authService.resetPassword(resetPassword, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
    }


    // ---- Tests for forgotPassword(String email) ----
    @Test
    void testForgotPassword_UserNotFound() {
        String email = "nonexistent@example.com";
        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();

        // Simulate a GET call to fetch user data, but return an empty list.
        ResponseEntity<List> getResponse = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(contains("/users?email="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(List.class)))
                .thenReturn(getResponse);

        ResponseEntity<String> response = authServiceSpy.forgotPassword(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testForgotPassword_Successful() {
        String email = "user@example.com";
        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", "mockUserId");
        ResponseEntity<List> getResponse = new ResponseEntity<>(Collections.singletonList(userMap), HttpStatus.OK);
        when(restTemplate.exchange(contains("/users?email="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(List.class)))
                .thenReturn(getResponse);

        when(restTemplate.exchange(contains("/users/mockUserId/execute-actions-email"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        ResponseEntity<String> response = authServiceSpy.forgotPassword(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset email sent successfully.", response.getBody());
    }


    // ---- Tests for getAllUsers() ----

    /*
    @Test
    void testGetAllUsers() {
        // Create a dummy Keycloak user representation.
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user1");
        userRep.setUsername("salma");
        userRep.setFirstName("salma");
        userRep.setLastName("harbi");
        userRep.setEmail("salma@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("finance"));
        userRep.setAttributes(attributes);

        // Simulate listing users.
        when(usersResource.list()).thenReturn(Collections.singletonList(userRep));

        // Simulate role retrieval for the user.
        when(usersResource.get("user1")).thenReturn(userResource);

        // Create a mock for RoleMappingResource.
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);

        // Use our extended interface instead of plain RolesResource,
        ExtendedRoleScopeResource rolesResource = mock(ExtendedRoleScopeResource.class);

        // Tell the userResource to return our roleMappingResource.
        when(userResource.roles()).thenReturn(roleMappingResource);

        // Stub the chain: When realmLevel() is called, return our extended mock.
        when(roleMappingResource.realmLevel()).thenReturn(rolesResource);

        // Create a dummy role representation and stub the listEffective call.
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("SPOC");
        when(rolesResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        // Act
        ResponseEntity<List<UserDto>> response = authService.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        UserDto userDto = response.getBody().get(0);
        assertEquals("salma", userDto.getUsername());
        assertEquals("salma", userDto.getFirstName());
        assertEquals("harbi", userDto.getLastName());
        assertEquals("salma@example.com", userDto.getEmail());
        assertEquals("finance", userDto.getDepartment());
        assertTrue(userDto.getRoles().contains("SPOC"));
    }

     */


    // --------------------- Tests for getUserByDepartment ---------------------
    @Test
    void testGetUserByDepartment_Success() {
        // Create a dummy UserRepresentation with a SPOC role and department "Engineering"
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user1");
        userRep.setUsername("spocUser");
        userRep.setFirstName("Alice");
        userRep.setLastName("Smith");
        userRep.setEmail("alice@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("digital&data"));
        userRep.setAttributes(attributes);

        // When listing users, return the dummy user.
        when(usersResource.list()).thenReturn(Collections.singletonList(userRep));

        // Stub role lookup chain for user1:
        when(usersResource.get("user1")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("SPOC");
        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        // Call with a list including "Engineering"
        List<UserDto> result = authService.getUserByDepartment(Collections.singletonList("digital&data"));

        assertNotNull(result);
        assertEquals(1, result.size());
        UserDto userDto = result.get(0);
        assertEquals("spocUser", userDto.getUsername());
        assertEquals("Alice", userDto.getFirstName());
        assertEquals("Smith", userDto.getLastName());
        assertEquals("alice@example.com", userDto.getEmail());
        assertEquals("digital&data", userDto.getDepartment());
        assertTrue(userDto.getRoles().contains("SPOC"));
    }

    /*
    @Test
    void testGetUserByDepartment_NoMatch() {
        // Create a user that does not have the SPOC role.
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user2");
        userRep.setUsername("normalUser");
        userRep.setFirstName("Bob");
        userRep.setLastName("Jones");
        userRep.setEmail("bob@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("Sales"));
        userRep.setAttributes(attributes);
        when(usersResource.list()).thenReturn(Collections.singletonList(userRep));

        // Stub role lookup: assign a generic "USER" role.
        when(usersResource.get("user2")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("USER");
        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        // Expect exception because no user has the SPOC role.
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                authService.getUserByDepartment(Collections.singletonList("Sales"))
        );
        assertTrue(thrown.getMessage().contains("No SPOC users found"));
    }

     */


    // --------------------- Tests for getUserByEmail(email) ---------------------
    @Test
    void testGetUserByEmail_Success() {
        String email = "test@example.com";
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user3");
        userRep.setUsername("testUser");
        userRep.setFirstName("Charlie");
        userRep.setLastName("Brown");
        userRep.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("Marketing"));
        userRep.setAttributes(attributes);

        // Stub the search call.
        when(usersResource.search(null, null, null, email, 0, 100))
                .thenReturn(Collections.singletonList(userRep));

        when(usersResource.get("user3")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("ADMIN");
        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        UserDto result = authService.getUserByEmail(email);
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("Charlie", result.getFirstName());
        assertEquals("Brown", result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals("Marketing", result.getDepartment());
        assertTrue(result.getRoles().contains("ADMIN"));
    }

    /*
    @Test
    void testGetUserByEmail_NotFound() {
        String email = "notfound@example.com";
        // Return an empty search result.
        when(usersResource.search(null, null, null, email, 0, 100))
                .thenReturn(Collections.emptyList());
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                authService.getUserByEmail(email)
        );
        assertTrue(thrown.getMessage().contains("User not found with email"));
    }

     */


    // --------------------- Tests for affectProjectToInterlocutor ---------------------
    @Test
    void testAffectProjectToInterlocutor_Success() {
        String username = "interlocutor";
        Long projectId = 100L;

        // Create a dummy User entity
        User dummyUser = new User();
        dummyUser.setUsername(username);
        dummyUser.setProjectsId(new ArrayList<>());
        when(userRepository.findUserByUsername(username)).thenReturn(dummyUser);

        authService.affectProjectToInterlocutor(username, projectId);

        assertTrue(dummyUser.getProjectsId().contains(projectId));
        verify(userRepository).save(dummyUser);
    }

    /*
    @Test
    void testAffectProjectToInterlocutor_UserNotFound() {
        String username = "nonexistent";
        Long projectId = 200L;
        when(userRepository.findUserByUsername(username)).thenReturn(null);
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                authService.affectProjectToInterlocutor(username, projectId)
        );
        assertTrue(thrown.getMessage().contains("No interlocutor founded by username"));
    }


     */

    // --------------------- Tests for getUserByUsername ---------------------
    @Test
    void testGetUserByUsername_Success() {
        String username = "johnDoe";
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user4");
        userRep.setUsername(username);
        userRep.setFirstName("John");
        userRep.setLastName("Doe");
        userRep.setEmail("john.doe@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("HR"));
        userRep.setAttributes(attributes);

        when(usersResource.search(username, 0, 100))
                .thenReturn(Collections.singletonList(userRep));

        when(usersResource.get("user4")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("OWNER");
        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        UserDto result = authService.getUserByUsername(username);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("HR", result.getDepartment());
        assertTrue(result.getRoles().contains("OWNER"));
    }

    /*
    @Test
    void testGetUserByUsername_NotFound() {
        String username = "missingUser";
        when(usersResource.search(username, 0, 100))
                .thenReturn(Collections.emptyList());
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                authService.getUserByUsername(username)
        );

        assertTrue(thrown.getMessage().contains("User not found") ||
                thrown.getMessage().contains(username));
    }

     */


    // ------------------- Test for getUsersByDepartment(String department) -------------------
    @Test
    void testGetUsersByDepartment() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user1");
        userRep.setUsername("deptUser");
        userRep.setFirstName("Dana");
        userRep.setLastName("Green");
        userRep.setEmail("dana.green@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("Finance"));
        userRep.setAttributes(attributes);

        // Return a list of users containing our dummy user.
        when(usersResource.list()).thenReturn(Collections.singletonList(userRep));

        when(usersResource.get("user1")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);

        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.emptyList());

        // Act: invoke the method for department "Finance".
        List<UserDto> result = authService.getUsersByDepartment("Finance");
        assertNotNull(result);
        assertEquals(1, result.size());

        UserDto userDto = result.get(0);
        assertEquals("deptUser", userDto.getUsername());
        assertEquals("Dana", userDto.getFirstName());
        assertEquals("Green", userDto.getLastName());
        assertEquals("dana.green@example.com", userDto.getEmail());
        assertEquals("Finance", userDto.getDepartment());
    }


    // ------------------- Test for getAllRoles() -------------------
    @Test
    void testGetAllRoles() {
        // Create dummy role representations.
        RoleRepresentation role1 = new RoleRepresentation();
        role1.setName("ADMIN");
        RoleRepresentation role2 = new RoleRepresentation();
        role2.setName("USER");

        // We assume that realmResource returns roles() directly.
        RolesResource rolesResource = mock(RolesResource.class);
        when(keycloakConfig.keycloak().realm(anyString()).roles()).thenReturn(rolesResource);
        when(rolesResource.list()).thenReturn(List.of(role1, role2));

        List<String> roles = authService.getAllRoles();
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
    }


    // ------------------- Test for refreshAccessToken(RefreshTokenDTO refreshToken) -------------------
    @Test
    void testRefreshAccessToken() {
        // Create a dummy RefreshTokenDTO
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken("dummyRefreshToken");

        // Simulate the endpoint URL.
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", "https://test-url.com/", "testRealm");

        // Build expected parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, REFRESH_TOKEN);
        params.add(CLIENT_ID, "testClientId");
        params.add(CLIENT_SECRET, "testClientSecret");
        params.add(REFRESH_TOKEN, "dummyRefreshToken");

        // Create headers with form URL encoding
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare request entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Simulate a response from RestTemplate
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(ACCESS_TOKEN, "newAccessToken");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseMap, HttpStatus.OK);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        Map result = authService.refreshAccessToken(refreshTokenDTO);
        assertNotNull(result);
        assertEquals("newAccessToken", result.get(ACCESS_TOKEN));
    }


    // ------------------- Test for signUp(UserRegistrationDTO dto) -------------------
    @Test
    void testSignUp_Success() {
        // Create a dummy DTO for registration.
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("newuser");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setEmail("new.user@example.com");
        dto.setDepartment("IT");
        dto.setPassword("1234");

        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();

        doNothing().when(authServiceSpy).setUserPassword(anyString(), anyString(), anyString());

        ResponseEntity<String> postResponse = new ResponseEntity<>("Created", HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(postResponse);

        // Simulate that no user exists, so repository returns null, and then we simulate a save.
        when(userRepository.findUserByUsername(dto.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);


        // Stub the part that fetches all users afterward
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user10");
        userRep.setUsername("newuser");
        userRep.setFirstName("New");
        userRep.setLastName("User");
        userRep.setEmail("new.user@example.com");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", Collections.singletonList("IT"));
        userRep.setAttributes(attributes);
        when(usersResource.list()).thenReturn(Collections.singletonList(userRep));
        when(usersResource.get("user10")).thenReturn(userResource);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        ExtendedRoleScopeResource extendedRoleScopeResource = mock(ExtendedRoleScopeResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(extendedRoleScopeResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("USER");
        when(extendedRoleScopeResource.listEffective()).thenReturn(Collections.singletonList(roleRep));

        // Act: call signUp and verify no exception is thrown
        assertDoesNotThrow(() -> authServiceSpy.signUp(dto));
    }

    /*
    @Test
    void testSignUp_Failure() {
        // Create a dummy DTO.
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("failuser");
        dto.setFirstName("Fail");
        dto.setLastName("User");
        dto.setEmail("fail.user@example.com");
        dto.setDepartment("HR");
        dto.setPassword("1234");

        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();
        doNothing().when(authServiceSpy).setUserPassword(anyString(), anyString(), anyString());

        ResponseEntity<String> postResponse = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(postResponse);

        // Act & Assert. Expect SignUpBadRequestException; if response is null, NPE will be thrown.
        SignUpBadRequestException thrown = assertThrows(SignUpBadRequestException.class, () ->
                authServiceSpy.signUp(dto)
        );
        assertTrue(thrown.getMessage().contains("Error creating user"));
    }

     */


    // ------------------- Test for testAssignRolesToUser() -------------------

    /*
    @Test
    void testAssignRolesToUser_Success() {

        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();

        // Stub user lookup.
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user123");
        UserRepresentation[] userArray = new UserRepresentation[]{userRep};
        ResponseEntity<UserRepresentation[]> userResponse =
                new ResponseEntity<>(userArray, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(UserRepresentation[].class)))
                .thenReturn(userResponse);

        for (String roleName : List.of("USER", "ADMIN")) {
            RoleRepresentation roleRep = new RoleRepresentation();
            roleRep.setName(roleName);
            ResponseEntity<RoleRepresentation> roleResponse =
                    new ResponseEntity<>(roleRep, HttpStatus.OK);
            when(restTemplate.exchange(contains("/roles/" + roleName),
                    eq(HttpMethod.GET), any(HttpEntity.class),
                    eq(RoleRepresentation.class)))
                    .thenReturn(roleResponse);
        }

        // Stub the call for assigning roles.
        ResponseEntity<Void> assignResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(assignResponse);

        // Act: assign roles to a valid user.
        assertDoesNotThrow(() ->
                authServiceSpy.assignRolesToUser("testuser", List.of("USER", "ADMIN"))
        );
    }

     */


    /*
    @Test
    void testAssignRolesToUser_UserNotFound() {
        AuthServiceImpl authServiceSpy = spy(authService);
        doReturn("fakeAdminToken").when(authServiceSpy).getAdminToken();
        // Simulate that user search returns empty.
        ResponseEntity<UserRepresentation[]> userResponse =
                new ResponseEntity<>(new UserRepresentation[0], HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(UserRepresentation[].class)))
                .thenReturn(userResponse);

        // Expect a UserNotFoundException.
        assertThrows(UserNotFoundException.class, () ->
                authServiceSpy.assignRolesToUser("nonexistentUser", List.of("SPOC"))
        );
    }

     */


    // ------------------ Tests for setUserPassword ------------------

    /*
    @Test
    void testSetUserPassword_Success() {
        String username = "testuser";
        String newPassword = "newPass";
        String adminToken = "fakeAdminToken";

        // Simulate GET call to retrieve user by username.
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", "user123");
        List<Map<String, Object>> list = Collections.singletonList(userMap);
        ResponseEntity<List<Map<String, Object>>> getUserResponse =
                new ResponseEntity<>(list, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(getUserResponse);

        // Simulate PUT call to set password.
        ResponseEntity<String> passwordResponse =
                new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(passwordResponse);

        // Act: call setUserPassword and ensure no exception is thrown.
        assertDoesNotThrow(() -> authService.setUserPassword(username, newPassword, adminToken));
    }

     */

    /*
    @Test
    void testSetUserPassword_UserNotFound() {
        String username = "nonexistent";
        String newPassword = "newPass";
        String adminToken = "fakeAdminToken";

        // Simulate GET call returning an empty list.
        ResponseEntity<List<Map<String, Object>>> getUserResponse =
                new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(getUserResponse);

        // Expect a UserNotFoundException.
        assertThrows(UserNotFoundException.class, () ->
                authService.setUserPassword(username, newPassword, adminToken)
        );
    }

     */


    // ------------------ Test for getAdminToken ------------------
    @Test
    void testGetAdminToken_Success() {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN, "dummyAccessToken");
        ResponseEntity<Map> tokenResponse =
                new ResponseEntity<>(tokenMap, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);

        String token = authService.getAdminToken();
        assertEquals("dummyAccessToken", token);
    }


}

