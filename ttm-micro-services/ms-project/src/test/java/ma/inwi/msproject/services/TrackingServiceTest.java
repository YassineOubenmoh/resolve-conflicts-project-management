package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.enums.TrackingType;
import ma.inwi.msproject.exceptions.TrackingNotFoundException;
import ma.inwi.msproject.mappers.TrackingMapper;
import ma.inwi.msproject.repositories.TrackingRepository;
import ma.inwi.msproject.service.TrackingService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {


    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private TrackingMapper trackingMapper;

    @InjectMocks
    private TrackingService trackingService;

    private TrackingDto trackingDto;
    private Tracking tracking;

    @BeforeEach
    void setUp(){
        trackingDto = TrackingDto.builder()
                .id(1L)
                .trackingType(TrackingType.FULL_TRACK)
                .build();

        tracking = Tracking.builder()
                .trackingType(TrackingType.FULL_TRACK)
                .build();
    }


    //Tests : Add tracking
    @Test
    void testAddTracking_Success() {
        // Mock repository behavior
        when(trackingMapper.trackingDtoToTracking(trackingDto)).thenReturn(tracking);
        when(trackingMapper.trackingToTrackingDto(tracking)).thenReturn(trackingDto);
        when(trackingRepository.save(tracking)).thenReturn(tracking);

        // Execute method
        TrackingDto result = trackingService.addTracking(trackingDto);
        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(trackingDto.getId(), result.getId());
        verify(trackingRepository, times(1)).save(tracking);
    }



    //Tests : Get All Trackings
    @Test
    void testGetAllTrackings_Success() {
        // Mock repository behavior
        Set<Tracking> trackings = new HashSet<>();
        trackings.add(tracking);

        // Convert the Set to a List
        when(trackingRepository.findAll()).thenReturn(new ArrayList<>(trackings));
        when(trackingMapper.trackingToTrackingDto(tracking)).thenReturn(trackingDto);

        // Execute method
        Set<TrackingDto> result = trackingService.getAllTrackings();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(trackingDto));
        verify(trackingRepository, times(1)).findAll();
        verify(trackingMapper, times(1)).trackingToTrackingDto(tracking);
    }



    /*
    @Test
    void testGetAllTrackings_EmptyList() {
        Set<Tracking> trackings = new HashSet<>();
        when(trackingRepository.findAll()).thenReturn(new ArrayList<>(trackings));

        Set<TrackingDto> result = trackingService.getAllTrackings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trackingRepository, times(1)).findAll();
    }

     */



    @Test
    void testGetTrackingById_Success() {
        // Mock repository behavior
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        when(trackingMapper.trackingToTrackingDto(tracking)).thenReturn(trackingDto);

        // Execute method
        TrackingDto result = trackingService.getTrackingById(1L);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(trackingDto.getId(), result.getId());
        verify(trackingRepository, times(1)).findById(1L);
        verify(trackingMapper, times(1)).trackingToTrackingDto(tracking);
    }

    @Test
    void testGetTrackingById_TrackingNotFound() {
        // Mock repository behavior for non-existing gate
        when(trackingRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute method and assert exception
        TrackingNotFoundException exception = assertThrows(TrackingNotFoundException.class, () -> {
            trackingService.getTrackingById(1L);
        });

        // Verify the exception message
        verify(trackingRepository, times(1)).findById(1L);
        verify(trackingMapper, never()).trackingToTrackingDto(any());
    }


    @Test
    void testDeleteTracking_Success() {
        // Given
        Long trackingId = 1L;
        Tracking tracking = new Tracking();
        tracking.setId(trackingId);
        tracking.setDeleted(false);  // Initially not deleted

        when(trackingRepository.findById(trackingId)).thenReturn(Optional.of(tracking));
        when(trackingRepository.save(tracking)).thenReturn(tracking);

        // When
        trackingService.deleteTracking(trackingId);

        // Then
        assertTrue(tracking.isDeleted());  // Ensure that the tracking is marked as deleted
        verify(trackingRepository, times(1)).save(tracking);  // Ensure save was called once
    }

    @Test
    void testDeleteTracking_NotFound() {
        // Given
        Long trackingId = 1L;
        when(trackingRepository.findById(trackingId)).thenReturn(Optional.empty());

        // When & Then
        TrackingNotFoundException exception = assertThrows(TrackingNotFoundException.class, () -> {
            trackingService.deleteTracking(trackingId);
        });

        // Ensure the exception message matches
        assertEquals("Tracking with 1 was not found !", exception.getMessage());

        // Verify no save operation was performed
        verify(trackingRepository, never()).save(any(Tracking.class));
    }


}