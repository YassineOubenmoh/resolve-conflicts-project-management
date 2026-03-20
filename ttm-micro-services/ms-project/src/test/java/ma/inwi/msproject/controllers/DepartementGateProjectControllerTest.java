package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.DepartementGateProjectController;
import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.service.DepartementGateProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(DepartementGateProjectController.class)
class DepartementGateProjectControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartementGateProjectService departementGateProjectService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartementGateProjectDto departementGateProjectDto;

    @BeforeEach
    void setUp() {
        departementGateProjectDto = DepartementGateProjectDto.builder()
                .id(1L)
                .gateProjectId(2L)
                .departementId(3L)
                .requiredActionLabels(Set.of("Required Action 1", "Required Action 2"))
                .build();
    }

    @Test
    void testAffectGateProjectToDepartement() throws Exception {
        Mockito.when(departementGateProjectService.affectGateProjectToDepartement(anyLong(), anyLong(), anyString()))
                .thenReturn(departementGateProjectDto);

        mockMvc.perform(post("/departement-gateproject/2/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.gateProjectId").value(2L))
                .andExpect(jsonPath("$.departementId").value(3L));
    }

    @Test
    void testGetAllProjectGatesAffectationsToDepartement() throws Exception {
        Mockito.when(departementGateProjectService.getAllProjectGatesAffectationsToDepartement())
                .thenReturn(Collections.singleton(departementGateProjectDto));

        mockMvc.perform(get("/departement-gateproject/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetDepartementGateProjectAffectationById() throws Exception {
        Mockito.when(departementGateProjectService.getDepartementGateProjectAffectationById(anyLong()))
                .thenReturn(departementGateProjectDto);

        mockMvc.perform(get("/departement-gateproject/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testModifyAffectedGateToDepartement() throws Exception {
        Mockito.when(departementGateProjectService.modifyAffectedGateToDepartement(anyLong(), anyLong()))
                .thenReturn(departementGateProjectDto);

        mockMvc.perform(put("/departement-gateproject/update-gate/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateGateProjectAffectationToDepartement() throws Exception {
        Mockito.when(departementGateProjectService.updateGateProjectAffectationToDepartement(anyLong(), any()))
                .thenReturn(departementGateProjectDto);

        mockMvc.perform(put("/departement-gateproject/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departementGateProjectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteGateProjectAffectationToDepartement() throws Exception {
        Mockito.doNothing().when(departementGateProjectService).deleteGateProjectAffectationToDepartement(anyLong());

        mockMvc.perform(delete("/departement-gateproject/delete/1"))
                .andExpect(status().isNoContent());
    }

 */
}
