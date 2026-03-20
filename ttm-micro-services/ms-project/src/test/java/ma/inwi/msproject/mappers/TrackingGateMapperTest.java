package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.TrackingGate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TrackingGateMapperTest {

    private TrackingGateMapper trackingGateMapper;
    private GateProjectDto gateProjectDto;
    private GateProject gateProject;

    @BeforeEach
    void setUp() {
        trackingGateMapper = Mappers.getMapper(TrackingGateMapper.class);
    }

    @Test
    void testEmptyGateProjects() {
        // Given
        TrackingGate trackingGate = TrackingGate.builder()
                .gateProjects(Set.of())
                .build();

        // When
        TrackingGateDto dto = trackingGateMapper.trackingGateToTrackingGateDto(trackingGate);

        // Then
        assertThat(dto.getGateProjectIds()).isEmpty();
    }

    @Test
    void testEmptyGateProjectIds() {
        // Given
        TrackingGateDto dto = TrackingGateDto.builder()
                .gateProjectIds(Set.of())
                .build();

        // When
        TrackingGate trackingGate = trackingGateMapper.trackingGateDtoToTrackingGate(dto);

        // Then
        assertThat(trackingGate.getGateProjects()).isEmpty();
    }

    @Test
    void testPartialNullRelationships() {
        // Given
        TrackingGate trackingGate = TrackingGate.builder()
                .gate(Gate.builder().id(10L).build())
                .tracking(null)
                .gateBefore(null)
                .gateAfter(Gate.builder().id(40L).build())
                .build();

        // When
        TrackingGateDto dto = trackingGateMapper.trackingGateToTrackingGateDto(trackingGate);

        // Then
        assertThat(dto.getGateId()).isEqualTo(10L);
        assertThat(dto.getTrackingId()).isNull();
        assertThat(dto.getGateBeforeId()).isNull();
        assertThat(dto.getGateAfterId()).isEqualTo(40L);
    }
}