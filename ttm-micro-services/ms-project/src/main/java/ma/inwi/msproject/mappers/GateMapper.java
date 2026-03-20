package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.TrackingGate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GateMapper {

    GateMapper INSTANCE = Mappers.getMapper(GateMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "gateType", target = "gateType")
    @Mapping(source = "trackingGates", target = "trackingGateIds", qualifiedByName = "mapTrackingGatesToTrackingGateIds")
    GateDto gateToGateDto(Gate gate);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "gateType", target = "gateType")
    @Mapping(source = "trackingGateIds", target = "trackingGates", qualifiedByName = "mapTrackingGateIdsToTrackingGates")
    Gate gateDtoToGate(GateDto gateDto);

    @Named("mapTrackingGatesToTrackingGateIds")
    default Set<Long> mapTrackingGatesToTrackingGateIds(Set<TrackingGate> trackingGates) {
        if (trackingGates == null) {
            return null;
        }
        return trackingGates.stream()
                .map(TrackingGate::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapTrackingGateIdsToTrackingGates")
    default Set<TrackingGate> mapTrackingGateIdsToTrackingGates(Set<Long> trackingGateIds) {
        if (trackingGateIds == null) {
            return null;
        }
        return trackingGateIds.stream()
                .map(trackingGateId -> {
                    TrackingGate trackingGate = new TrackingGate();
                    trackingGate.setId(trackingGateId);
                    return trackingGate;
                })
                .collect(Collectors.toSet());
    }

}