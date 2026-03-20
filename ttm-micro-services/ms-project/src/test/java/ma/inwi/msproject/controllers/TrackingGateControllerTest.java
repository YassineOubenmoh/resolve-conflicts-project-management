package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.TrackingGateController;
import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.service.TrackingGateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles("test")
//@WebMvcTest(TrackingGateController.class)
class TrackingGateControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrackingGateService trackingGateService;

    @Autowired
    private ObjectMapper objectMapper;

    private TrackingGateDto trackingGateDto;


    @BeforeEach
    void setUp() {
        trackingGateDto = new TrackingGateDto();
        trackingGateDto.setId(10L);
        trackingGateDto.setGateId(1L);
        trackingGateDto.setTrackingId(2L);

    }

    @Test
    void testAffectGateToTracking_Success() throws Exception {
        // Mock data
        TrackingGateDto trackingGateDto = new TrackingGateDto();
        trackingGateDto.setGateId(1L);
        trackingGateDto.setTrackingId(2L);

        when(trackingGateService.affectGateToTracking(1L, 2L)).thenReturn(trackingGateDto);

        mockMvc.perform(post("/tracking-gate/2/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gateId").value(1L))
                .andExpect(jsonPath("$.trackingId").value(2L));

        verify(trackingGateService, times(1)).affectGateToTracking(1L, 2L);
    }

    @Test
    void testGetRelatedGatesToTracking_Success() throws Exception {
        Set<TrackingGateDto> trackingGateDtos = new HashSet<>();
        trackingGateDtos.add(new TrackingGateDto(1L, 1L, 2L, null, null, new HashSet<>()));

        when(trackingGateService.getRelatedGatesToTracking(2L)).thenReturn(trackingGateDtos);

        mockMvc.perform(get("/tracking-gate/find/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].gateId").value(1L))
                .andExpect(jsonPath("$[0].trackingId").value(2L));

        verify(trackingGateService, times(1)).getRelatedGatesToTracking(2L);
    }

 */
}