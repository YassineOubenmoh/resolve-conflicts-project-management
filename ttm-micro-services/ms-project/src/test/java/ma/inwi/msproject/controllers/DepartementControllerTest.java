package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.DepartementController;
import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.service.DepartementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(DepartementController.class)
class DepartementControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartementService departementService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartementDto departementDto;

    @BeforeEach
    void setUp() {
        departementDto = new DepartementDto(1L, "IT", Collections.singleton(1L));
    }

    @Test
    void testAddDepartement() throws Exception {
        when(departementService.addDepartement(any(DepartementDto.class))).thenReturn(departementDto);

        mockMvc.perform(post("/departement/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departementDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(departementDto.getId()))
                .andExpect(jsonPath("$.departement").value(departementDto.getDepartement()));
    }

    @Test
    void testGetAllDepartements() throws Exception {
        when(departementService.getAllDepartements()).thenReturn(Set.of(departementDto));

        mockMvc.perform(get("/departement/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(departementDto.getId()))
                .andExpect(jsonPath("$[0].departement").value(departementDto.getDepartement()));
    }

    @Test
    void testGetDepartementById() throws Exception {
        when(departementService.getDepartementById(anyLong())).thenReturn(departementDto);

        mockMvc.perform(get("/departement/find/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(departementDto.getId()))
                .andExpect(jsonPath("$.departement").value(departementDto.getDepartement()));
    }

    @Test
    void testUpdateDepartement() throws Exception {
        when(departementService.updateDepartement(anyLong(), any(DepartementDto.class))).thenReturn(departementDto);

        mockMvc.perform(put("/departement/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departementDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(departementDto.getId()))
                .andExpect(jsonPath("$.departement").value(departementDto.getDepartement()));
    }

    @Test
    void testDeleteDepartement() throws Exception {
        doNothing().when(departementService).deleteDepartement(anyLong());

        mockMvc.perform(delete("/departement/delete/{id}", 1L))
                .andExpect(status().isNoContent());
    }

 */
}
