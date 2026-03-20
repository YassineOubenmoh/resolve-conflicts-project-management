package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.RequiredActionController;
import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.dto.RequiredActionGlobalResponse;
import ma.inwi.msproject.service.RequiredActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(RequiredActionController.class)
class RequiredActionControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequiredActionService requiredActionService;

    private RequiredActionDto requiredActionDto;
    private RequiredActionGlobalResponse requiredActionGlobalResponse;

    @BeforeEach
    void setUp() {
        requiredActionDto = RequiredActionDto.builder()
                .id(1L)
                .requiredAction("Test Action")
                .departementGateProjectId(2L)
                .build();

        requiredActionGlobalResponse = RequiredActionGlobalResponse.builder()
                .requiredAction("Test Action")
                .departement("IT")
                .gateType(null)
                .projectTitle("Test Project")
                .build();
    }

    @Test
    void testAddRequiredAction() throws Exception {
        when(requiredActionService.addActionRequise(any(RequiredActionDto.class))).thenReturn(requiredActionDto);
        mockMvc.perform(post("/required-action/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requiredActionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requiredAction").value("Test Action"))
                .andExpect(jsonPath("$.departementGateProjectId").value(2));
    }

    @Test
    void testGetAllRequiredActions() throws Exception {
        when(requiredActionService.getAllActionRequises()).thenReturn(Set.of(requiredActionDto));
        mockMvc.perform(get("/required-action/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].requiredAction").value("Test Action"));
    }

    @Test
    void testGetRequiredActionById() throws Exception {
        when(requiredActionService.getActionRequiseById(1L)).thenReturn(requiredActionDto);
        mockMvc.perform(get("/required-action/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requiredAction").value("Test Action"));
    }

    @Test
    void testUpdateRequiredAction() throws Exception {
        when(requiredActionService.updateActionRequise(any(Long.class), any(RequiredActionDto.class)))
                .thenReturn(requiredActionDto);
        mockMvc.perform(put("/required-action/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requiredActionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requiredAction").value("Test Action"));
    }

    @Test
    void testDeleteRequiredAction() throws Exception {
        mockMvc.perform(delete("/required-action/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRequiredActionGlobalInfoById() throws Exception {
        when(requiredActionService.getRequiredActionGlobalInfoById(1L)).thenReturn(requiredActionGlobalResponse);
        mockMvc.perform(get("/required-action/global-response/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredAction").value("Test Action"))
                .andExpect(jsonPath("$.departement").value("IT"))
                .andExpect(jsonPath("$.projectTitle").value("Test Project"));
    }

 */
}
