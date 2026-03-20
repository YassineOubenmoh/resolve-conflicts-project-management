package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.GateProjectController;
import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.service.GateProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(GateProjectController.class)
class GateProjectControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GateProjectService gateProjectService;

    private GateProjectDto gateProjectDto;

    @BeforeEach
    void setUp() {
        gateProjectDto = GateProjectDto.builder()
                .id(1L)
                .trackingGateId(100L)
                .currentGate(true)
                .passingDate(null)
                .inProgress(true)
                .startingGate(true)
                .decisionType(null)
                .information(Collections.emptySet())
                .actions(Collections.emptySet())
                .decisions(Collections.emptySet())
                .projectId(10L)
                .departementGateProjectIds(new HashSet<>())
                .build();
    }

    @Test
    void testAddGateProject() throws Exception {
        Mockito.when(gateProjectService.addGateProject(Mockito.any(GateProjectDto.class))).thenReturn(gateProjectDto);

        mockMvc.perform(post("/gate-project/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(gateProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.trackingGateId").value(100L))
                .andExpect(jsonPath("$.currentGate").value(true));
    }

    @Test
    void testGetAllGateProjects() throws Exception {
        Set<GateProjectDto> gateProjects = new HashSet<>(Collections.singletonList(gateProjectDto));
        Mockito.when(gateProjectService.getAllGateProjects()).thenReturn(gateProjects);

        mockMvc.perform(get("/gate-project/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].trackingGateId").value(100L));
    }

    @Test
    void testGetGateProjectById() throws Exception {
        Mockito.when(gateProjectService.getGateProjectById(1L)).thenReturn(gateProjectDto);

        mockMvc.perform(get("/gate-project/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.trackingGateId").value(100L));
    }

    @Test
    void testUpdateGateProject() throws Exception {
        Mockito.when(gateProjectService.updateGateProject(Mockito.eq(1L), Mockito.any(GateProjectDto.class))).thenReturn(gateProjectDto);

        mockMvc.perform(put("/gate-project/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(gateProjectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.trackingGateId").value(100L));
    }

    @Test
    void testDeleteGateProject() throws Exception {
        Mockito.doNothing().when(gateProjectService).deleteGateProject(1L);

        mockMvc.perform(delete("/gate-project/delete/1"))
                .andExpect(status().isNoContent());
    }

 */
}
