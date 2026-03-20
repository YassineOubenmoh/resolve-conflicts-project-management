package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.GateNotFoundException;
import ma.inwi.msproject.mappers.GateMapper;
import ma.inwi.msproject.repositories.GateRepository;
import ma.inwi.msproject.repositories.TrackingGateRepository;
import ma.inwi.msproject.service.GateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GateServiceTest {

    @Mock
    private GateRepository gateRepository;

    @Mock
    private GateMapper gateMapper;

    @Mock
    private TrackingGateRepository trackingGateRepository;

    @InjectMocks
    private GateService gateService;

    private GateDto gateDto;
    private Gate gate;

    private GateDto updatedGateDto;
    private Gate updatedGate;

    private TrackingGate trackingGate1;
    private TrackingGate trackingGate2;

    @BeforeEach
    void setUp() {
        gateDto = GateDto.builder()
                .id(1L)
                .gateType(GateType.T1)
                .trackingGateIds(Set.of(10L, 20L))
                .build();

        trackingGate1 = TrackingGate.builder().id(10L).build();
        trackingGate2 = TrackingGate.builder().id(20L).build();

        gate = Gate.builder()
                .id(1L)
                .gateType(GateType.T1)
                .trackingGates(Set.of(trackingGate1, trackingGate2))
                .build();

        updatedGateDto = GateDto.builder()
                .id(1L)
                .gateType(GateType.T2)
                .trackingGateIds(Set.of(10L, 20L))  // Example tracking IDs
                .build();

        updatedGate = Gate.builder()
                .id(1L)
                .gateType(GateType.T2)
                .trackingGates(Set.of(trackingGate1, trackingGate2))  // Example tracking IDs
                .build();
    }


    @Test
    void testAddGate_Success() {

        when(gateMapper.gateDtoToGate(gateDto)).thenReturn(gate);
        when(gateRepository.save(gate)).thenReturn(gate);
        when(gateMapper.gateToGateDto(gate)).thenReturn(gateDto);

        GateDto result = gateService.addGate(gateDto);

        assertNotNull(result);
        assertEquals(gateDto.getId(), result.getId());
        verify(gateRepository, times(1)).save(gate);
    }

    @Test
    void testUpdateGate_Success() {
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate));
        when(gateRepository.save(updatedGate)).thenReturn(updatedGate);
        when(gateMapper.gateToGateDto(updatedGate)).thenReturn(updatedGateDto);

        GateDto result = gateService.updateGate(1L, updatedGateDto);

        assertNotNull(result);
        assertEquals(gateDto.getId(), result.getId());
        verify(gateRepository, times(1)).save(updatedGate);
    }


    @Test
    void testUpdateGate_GateNotFound() {
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());

        GateNotFoundException exception = assertThrows(GateNotFoundException.class, () -> {
            gateService.updateGate(1L, updatedGateDto);
        });

        assertEquals("Gate with ID: 1 was not found!", exception.getMessage());
    }


    //Delete Tests
    @Test
    void testDeleteGate_Success() {
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate));

        gateService.deleteGate(1L);

        verify(gateRepository, times(1)).findById(1L);
    }


    @Test
    void testDeleteGate_GateNotFound() {
        // Mock repository behavior
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute method and assert exception
        GateNotFoundException exception = assertThrows(GateNotFoundException.class, () -> gateService.deleteGate(1L));
        assertEquals("Gate with 1 was not found !", exception.getMessage());

        // Verify interactions
        verify(gateRepository, times(1)).findById(1L);
        verify(gateRepository, times(0)).delete(any());
    }


    //Get All Tests
    /*
    @Test
    void testGetAllGates_Success() {
        // Mock repository behavior
        Set<Gate> gates = new HashSet<>();
        gates.add(gate);

        // Convert the Set to a List
        when(gateRepository.findAll()).thenReturn(new ArrayList<>(gates));
        when(gateMapper.gateToGateDto(gate)).thenReturn(gateDto);

        // Execute method
        Set<GateDto> result = gateService.getAllGates();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(gateDto));
        verify(gateRepository, times(1)).findAll();
        verify(gateMapper, times(1)).gateToGateDto(gate);
    }

     */



    /*
    @Test
    void testGetAllGates_EmptyList() {
        // Mock repository behavior
        Set<Gate> gates = new HashSet<>();
        when(gateRepository.findAll()).thenReturn(new ArrayList<>(gates));

        // Execute method
        Set<GateDto> result = gateService.getAllGates();

        // Verify interactions and assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gateRepository, times(1)).findAll();
    }
     */


    //Test Get By Id
    @Test
    void testGetGateById_Success() {
        // Mock repository behavior
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate));
        when(gateMapper.gateToGateDto(gate)).thenReturn(gateDto);

        // Execute method
        GateDto result = gateService.getGateById(1L);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(gateDto.getId(), result.getId());
        verify(gateRepository, times(1)).findById(1L);
        verify(gateMapper, times(1)).gateToGateDto(gate);
    }

    @Test
    void testGetGateById_GateNotFound() {
        // Mock repository behavior for non-existing gate
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute method and assert exception
        GateNotFoundException exception = assertThrows(GateNotFoundException.class, () -> {
            gateService.getGateById(1L);
        });

        assertEquals("Gate with 1 was not found !", exception.getMessage());
        verify(gateRepository, times(1)).findById(1L);
        verify(gateMapper, never()).gateToGateDto(any());
    }


    @Test
    void deleteGate_ShouldDeleteGate_WhenIdIsValid() {
        // Given
        Long id = 1L;
        Gate gate = new Gate();
        gate.setId(id);

        // Mocking repository methods
        when(gateRepository.findById(id)).thenReturn(Optional.of(gate));
        when(gateRepository.save(any(Gate.class))).thenReturn(gate);

        // Act
        gateService.deleteGate(id);

        // Assert
        assertTrue(gate.isDeleted());  // Ensure the gate is marked as deleted

        // Verifications
        verify(gateRepository, times(1)).findById(id);  // Verify the findById is called
        verify(gateRepository, times(1)).save(gate);   // Verify that save is called to persist the changes
    }

    @Test
    void deleteGate_ShouldThrowGateNotFoundException_WhenGateNotFound() {
        // Given
        Long id = 1L;

        // Mocking repository methods to return empty
        when(gateRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        GateNotFoundException exception = assertThrows(GateNotFoundException.class, () -> {
            gateService.deleteGate(id);
        });

        assertEquals("Gate with 1 was not found !", exception.getMessage());

        // Verifications
        verify(gateRepository, times(1)).findById(id);   // Verify findById is called once
        verify(gateRepository, times(0)).save(any(Gate.class));  // Verify save is not called
    }



}