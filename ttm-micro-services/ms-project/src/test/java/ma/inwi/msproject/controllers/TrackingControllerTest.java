package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.TrackingController;
import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.enums.TrackingType;
import ma.inwi.msproject.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(TrackingController.class)
class TrackingControllerTest {
/*
    @MockitoBean
    private TrackingService trackingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TrackingDto trackingDto;
    private TrackingDto updatedTrackingDto;

    @BeforeEach
    void setUp() {
        trackingDto = new TrackingDto();
        trackingDto.setId(1L);
        trackingDto.setTrackingType(TrackingType.FAST_TRACK);
        trackingDto.setTrackingGateIds(Set.of(100L, 200L));

        updatedTrackingDto = new TrackingDto();
        updatedTrackingDto.setId(1L);
        updatedTrackingDto.setTrackingType(TrackingType.FULL_TRACK);
        updatedTrackingDto.setTrackingGateIds(Set.of(100L, 200L));
    }

    @Test
    void testAddTracking() throws Exception {
        when(trackingService.addTracking(any(TrackingDto.class))).thenReturn(trackingDto);

        mockMvc.perform(post("/tracking/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(trackingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trackingType").value("FAST_TRACK"))
                .andExpect(jsonPath("$.trackingGateIds", containsInAnyOrder(100, 200)));
    }

    @Test
    void testGetAllTrackings() throws Exception {
        Set<TrackingDto> trackingDtos = Set.of(trackingDto);
        when(trackingService.getAllTrackings()).thenReturn(trackingDtos);

        mockMvc.perform(get("/tracking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].trackingType").value(containsInAnyOrder("FAST_TRACK")))
                .andExpect(jsonPath("$[*].trackingGateIds[*]", containsInAnyOrder(100, 200)));
    }

    @Test
    void testGetTrackingById() throws Exception {
        when(trackingService.getTrackingById(1L)).thenReturn(trackingDto);

        mockMvc.perform(get("/tracking/find/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trackingType").value("FAST_TRACK"))
                .andExpect(jsonPath("$.trackingGateIds", containsInAnyOrder(100, 200)));
    }

    @Test
    void testUpdateTracking_success() throws Exception {
        when(trackingService.updateTracking(1L, updatedTrackingDto)).thenReturn(updatedTrackingDto);

        mockMvc.perform(put("/tracking/update/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedTrackingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trackingType").value("FULL_TRACK"))
                .andExpect(jsonPath("$.trackingGateIds", containsInAnyOrder(100, 200)));
    }

    @Test
    void testDeleteTracking_success() throws Exception {
        doNothing().when(trackingService).deleteTracking(1L);

        mockMvc.perform(delete("/tracking/delete/{id}", 1L))
                .andExpect(status().isNoContent());
    }

 */
}
