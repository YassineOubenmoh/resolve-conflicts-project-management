package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.exceptions.GateNotFoundException;
import ma.inwi.msproject.exceptions.TrackingNotFoundException;
import ma.inwi.msproject.mappers.TrackingGateMapper;
import ma.inwi.msproject.repositories.GateRepository;
import ma.inwi.msproject.repositories.TrackingGateRepository;
import ma.inwi.msproject.repositories.TrackingRepository;
import ma.inwi.msproject.service.TrackingGateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingGateServiceTest {

    @Mock
    private TrackingGateRepository trackingGateRepository;

    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private GateRepository gateRepository;

    @Mock
    private TrackingGateMapper trackingGateMapper;

    @InjectMocks
    private TrackingGateService trackingGateService;

    private Tracking tracking;
    private TrackingGate trackingGate;
    private TrackingGateDto trackingGateDto;

    @BeforeEach
    void setUp() {
        tracking = new Tracking();
        tracking.setId(1L);

        trackingGate = new TrackingGate();
        trackingGate.setTracking(tracking);

        trackingGateDto = new TrackingGateDto();
    }

    @Test
    void getRelatedGatesToTracking_ShouldThrowException_WhenTrackingNotFound() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TrackingNotFoundException.class, () -> trackingGateService.getRelatedGatesToTracking(1L));

        verify(trackingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(trackingGateRepository, trackingGateMapper);
    }


    @Test
    void affectGateToTracking_ShouldThrowException_WhenGateNotFound() {
        // Arrange
        Long gateId = 1L;
        Long trackingId = 1L;

        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        // Act & Assert
        GateNotFoundException exception = assertThrows(GateNotFoundException.class, () -> {
            trackingGateService.affectGateToTracking(gateId, trackingId);
        });

        assertEquals("The gate with the id " + gateId + " was not found !", exception.getMessage());
        verify(gateRepository, times(1)).findById(gateId);
        verifyNoInteractions(trackingRepository);
        verifyNoInteractions(trackingGateRepository);
    }

    @Test
    void affectGateToTracking_ShouldThrowException_WhenTrackingNotFound() {
        // Arrange
        Long gateId = 1L;
        Long trackingId = 1L;

        Gate gate = new Gate();
        gate.setId(gateId);

        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(trackingRepository.findById(trackingId)).thenReturn(Optional.empty());

        // Act & Assert
        TrackingNotFoundException exception = assertThrows(TrackingNotFoundException.class, () -> {
            trackingGateService.affectGateToTracking(gateId, trackingId);
        });

        assertEquals("The tracking with id " + trackingId + " was not found !", exception.getMessage());
        verify(gateRepository, times(1)).findById(gateId);
        verify(trackingRepository, times(1)).findById(trackingId);
        verifyNoInteractions(trackingGateRepository);
    }


    @Test
    void putGatesInOrder_ShouldThrowException_WhenTrackingNotFound() {
        Long trackingId = 1L;

        when(trackingRepository.findById(trackingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trackingGateService.putGatesInOrder(trackingId);
        });

        assertEquals("Tracking not found with id: " + trackingId, exception.getMessage());

        // Verify interaction with tracking repository
        verify(trackingRepository, times(1)).findById(trackingId);
        verifyNoInteractions(trackingGateRepository);  // No interaction with trackingGateRepository
    }




}