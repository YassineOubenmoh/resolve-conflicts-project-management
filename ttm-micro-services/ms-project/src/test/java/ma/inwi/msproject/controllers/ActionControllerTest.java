package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.client.ProjectRestClient;
import ma.inwi.msproject.controller.ActionController;
import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.dto.DocumentDto;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.repositories.ActionRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import ma.inwi.msproject.service.ActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActionController.class)
class ActionControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActionService actionService;

    @MockitoBean
    private ProjectRestClient projectRestClient;

    @MockitoBean
    private RequiredActionRepository requiredActionRepository;

    @MockitoBean
    private ActionRepository actionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ActionDto actionDto;
    private RequiredAction requiredAction;
    private JwtAuthenticationToken jwtAuth;

    @BeforeEach
    void setup() {
        actionDto = new ActionDto();
        actionDto.setId(1L);
        actionDto.setActionLabel("TestActionLabel");
        actionDto.setRequiredActionId(99L);

        // Setup project
        Project project = new Project();
        project.setId(10L);

        // Setup gate
        Gate gate = new Gate();
        gate.setGateType(GateType.T1);

        TrackingGate trackingGate = new TrackingGate();
        trackingGate.setGate(gate);

        // Setup gateProject
        GateProject gateProject = new GateProject();
        gateProject.setProject(project);
        gateProject.setTrackingGate(trackingGate);

        // Setup departement
        Departement departement = new Departement();
        departement.setDepartement("Tech");

        DepartementGateProject dgp = new DepartementGateProject();
        dgp.setDepartement(departement);
        dgp.setGateProject(gateProject);

        requiredAction = new RequiredAction();
        requiredAction.setId(99L);
        requiredAction.setDepartementGateProject(dgp);
        requiredAction.setActions(Set.of());

        // Setup fake JWT
        Jwt jwt = Jwt.withTokenValue("token")
                .claim("preferred_username", "test_user")
                .claim("email", "user@example.com")
                .header("alg", "none")
                .build();

        jwtAuth = new JwtAuthenticationToken(jwt);

        when(requiredActionRepository.findById(99L)).thenReturn(Optional.of(requiredAction));
    }

    @Test
    void testAddAction_success() throws Exception {
        MockMultipartFile actionFile = new MockMultipartFile("actionDocument", "test.txt", "text/plain", "Sample content".getBytes());
        MockMultipartFile actionDtoFile = new MockMultipartFile("actionDto", "", "application/json", objectMapper.writeValueAsBytes(actionDto));

        DocumentDto uploadedDoc = DocumentDto.builder()
                .id(1L)
                .documentLabel("test.txt")
                .projectId(10L)
                .typeDocument("text/plain")
                .dateUpload("2025-06-12")
                .size("123MB")
                .department("Tech")
                .authorName("test_user")
                .gateLabel(GateType.T1)
                .build();

        when(actionService.uploadFile(any())).thenReturn(uploadedDoc);
        when(actionService.updateDocument(any(), any())).thenReturn(uploadedDoc);
        when(actionService.addAction(any())).thenReturn(actionDto);

        mockMvc.perform(multipart("/action/add")
                        .file(actionFile)
                        .file(actionDtoFile)
                        .with(authentication(jwtAuth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(actionDto.getId()))
                .andExpect(jsonPath("$.actionLabel").value(actionDto.getActionLabel()));
    }

    @Test
    void testAddAction_missingRequiredAction_shouldReturnNotFound() throws Exception {
        actionDto.setRequiredActionId(123L);

        MockMultipartFile actionFile = new MockMultipartFile("actionDocument", "test.txt", "text/plain", "Sample content".getBytes());
        MockMultipartFile actionDtoFile = new MockMultipartFile("actionDto", "", "application/json", objectMapper.writeValueAsBytes(actionDto));

        when(requiredActionRepository.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(multipart("/action/add")
                        .file(actionFile)
                        .file(actionDtoFile)
                        .with(authentication(jwtAuth)))
                .andExpect(status().isInternalServerError()); // Controller throws exception
    }

 */
}
