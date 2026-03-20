package ma.inwi.ms_iam.controller;

import ma.inwi.ms_iam.dto.LoginDto;
import ma.inwi.ms_iam.dto.TokenDto;
import ma.inwi.ms_iam.dto.UserDto;
import ma.inwi.ms_iam.dto.UserRegistrationDTO;
import ma.inwi.ms_iam.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Test
    void login_shouldReturnTokenDto() {
        // Arrange
        LoginDto loginDto = new LoginDto("user", "pass");
        TokenDto expectedToken = new TokenDto("accessToken", "refreshToken");
        when(authService.login(loginDto)).thenReturn(expectedToken);

        // Act
        TokenDto actualToken = authController.login(loginDto);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(authService).login(loginDto);
    }

    @Test
    void signUp_shouldReturnCreatedStatus() {
        // Arrange
        UserRegistrationDTO user = new UserRegistrationDTO();
        doNothing().when(authService).signUp(user);

        // Act
        ResponseEntity<String> response = authController.signUp(user);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertEquals("User created successfully", response.getBody());
    }

    @Test
    void forgotPassword_shouldReturnSuccess() {
        // Arrange
        String email = "user@example.com";
        String expectedMessage = "Password reset mail sent";
        when(authService.forgotPassword(email)).thenReturn(ResponseEntity.ok(expectedMessage));

        // Act
        ResponseEntity<String> response = authController.forgotPassword(email);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    void getUserByEmail_shouldReturnUserDto() {
        // Arrange
        String email = "email@test.com";
        UserDto expectedUser = UserDto.builder().email(email).build();
        when(authService.getUserByEmail(email)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDto> response = authController.getUserByEmail(email);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(email, response.getBody().getEmail());
    }

    @Test
    void getUserByUsername_shouldReturnUserDto() {
        // Arrange
        String username = "abdelkrim";
        UserDto expectedUser = UserDto.builder().username(username).build();
        when(authService.getUserByUsername(username)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDto> response = authController.getUserByUsername(username);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(username, response.getBody().getUsername());
    }

    @Test
    void getUsersByDepartments_shouldReturnList() {
        // Arrange
        List<String> departments = List.of("digital&data", "IT");
        List<UserDto> expectedUsers = List.of(UserDto.builder().department("IT").build());
        when(authService.getUserByDepartment(departments)).thenReturn(expectedUsers);

        // Act
        List<UserDto> result = authController.getUsersByDepartments(departments);

        // Assert
        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getDepartment());
    }

    @Test
    void affectProjectToInterlocutor_shouldReturnSuccessMessage() {
        // Arrange
        String username = "user";
        Long projectId = 10L;
        doNothing().when(authService).affectProjectToInterlocutor(username, projectId);

        // Act
        ResponseEntity<String> response = authController.affectProjectToInterlocutor(username, projectId);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("was affected to " + username));
    }
}
