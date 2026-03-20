package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.TrackingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackingMapperTest {

    private TrackingMapper trackingMapper;

    @BeforeEach
    void setUp() {
        trackingMapper = Mappers.getMapper(TrackingMapper.class);
    }

    @Test
    void testTrackingToTrackingDto() {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        tracking.setTrackingType(TrackingType.FULL_TRACK);

        TrackingGate trackingGate1 = new TrackingGate();
        trackingGate1.setId(10L);
        TrackingGate trackingGate2 = new TrackingGate();
        trackingGate2.setId(20L);
        tracking.setTrackingGates (Set.of(trackingGate1, trackingGate2));

        TrackingDto trackingDto = trackingMapper.trackingToTrackingDto(tracking);

        assertNotNull(trackingDto);
        assertEquals(tracking.getId(), trackingDto.getId());
        assertEquals(tracking.getTrackingType(), trackingDto.getTrackingType());
        assertEquals(Set.of(10L, 20L), trackingDto.getTrackingGateIds());
    }

    @Test
    void testTrackingDtoToTracking() {
        TrackingDto trackingDto = new TrackingDto();
        trackingDto.setId(1L);
        trackingDto.setTrackingType(TrackingType.FAST_TRACK);
        trackingDto.setTrackingGateIds(Set.of(10L, 20L));

        Tracking tracking = trackingMapper.trackingDtoToTracking(trackingDto);

        assertNotNull(tracking);
        assertEquals(trackingDto.getId(), tracking.getId());
        assertEquals(trackingDto.getTrackingType(), tracking.getTrackingType());
        assertEquals(2, tracking.getTrackingGates().size());
        assertTrue(tracking.getTrackingGates().stream().anyMatch(g -> g.getId().equals(10L)));
        assertTrue(tracking.getTrackingGates().stream().anyMatch(g -> g.getId().equals(20L)));
    }


}