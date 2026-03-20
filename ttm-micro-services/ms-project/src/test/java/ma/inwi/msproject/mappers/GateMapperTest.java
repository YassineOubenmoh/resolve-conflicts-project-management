package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.GateType;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GateMapperTest {

    private final GateMapper gateMapper = GateMapper.INSTANCE;

    @Test
    void shouldMapGateToGateDto() {
        // Given
        Gate gate = Gate.builder()
                .id(1L)
                .gateType(GateType.T1)
                .trackingGates(Set.of(
                        TrackingGate.builder().id(101L).build(),
                        TrackingGate.builder().id(102L).build()
                ))
                .build();

        // When
        GateDto gateDto = gateMapper.gateToGateDto(gate);

        // Then
        assertThat(gateDto).isNotNull();
        assertThat(gateDto.getId()).isEqualTo(1L);
        assertThat(gateDto.getGateType()).isEqualTo(GateType.T1);
    }

    @Test
    void shouldMapGateDtoToGate() {
        // Given
        GateDto gateDto = GateDto.builder()
                .id(2L)
                .gateType(GateType.T0)
                .trackingGateIds(Set.of(201L, 202L))
                .build();

        Gate gate = gateMapper.gateDtoToGate(gateDto);

        assertThat(gate).isNotNull();
        assertThat(gate.getId()).isEqualTo(2L);
        assertThat(gate.getGateType()).isEqualTo(GateType.T0);
    }
}