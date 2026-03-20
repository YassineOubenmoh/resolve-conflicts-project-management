package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.GateController;
import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.service.GateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(GateController.class)
class GateControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GateService gateService;

    @Autowired
    private ObjectMapper objectMapper;

    private GateDto gateDto;

    @BeforeEach
    void setUp() {
        gateDto = GateDto.builder()
                .id(1L)
                .gateType(GateType.T1)
                .trackingGateIds(Set.of(3L, 5L))
                .build();
    }

    @Test
    void testAddGate() throws Exception {
        when(gateService.addGate(any(GateDto.class))).thenReturn(gateDto);

        mockMvc.perform(post("/gate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(gateDto.getId()))
                .andExpect(jsonPath("$.gateType").value(gateDto.getGateType().toString()));
    }

    @Test
    void testGetAllGates() throws Exception {
        Set<GateDto> gateDtoSet = new HashSet<>();
        gateDtoSet.add(gateDto);
        when(gateService.getAllGates()).thenReturn(gateDtoSet);

        mockMvc.perform(get("/gate/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(gateDtoSet.size()));
    }

    @Test
    void testGetGateById() throws Exception {
        Long gateId = 1L;
        when(gateService.getGateById(gateId)).thenReturn(gateDto);

        mockMvc.perform(get("/gate/find/{id}", gateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gateDto.getId()))
                .andExpect(jsonPath("$.gateType").value(gateDto.getGateType().toString()));
    }

    @Test
    void testUpdateGate() throws Exception {
        Long gateId = 1L;
        when(gateService.updateGate(eq(gateId), any(GateDto.class))).thenReturn(gateDto);

        mockMvc.perform(put("/gate/update/{id}", gateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gateDto.getId()))
                .andExpect(jsonPath("$.gateType").value(gateDto.getGateType().toString()));
    }

    @Test
    void testDeleteGate() throws Exception {
        Long gateId = 1L;
        doNothing().when(gateService).deleteGate(gateId);

        mockMvc.perform(delete("/gate/delete/{id}", gateId))
                .andExpect(status().isNoContent());
    }

 */
}

