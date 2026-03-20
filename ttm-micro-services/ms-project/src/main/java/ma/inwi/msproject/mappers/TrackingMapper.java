package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.entities.TrackingGate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrackingMapper {

    TrackingMapper INSTANCE = Mappers.getMapper(TrackingMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackingType", target = "trackingType")
    @Mapping(source = "trackingGates", target = "trackingGateIds", qualifiedByName = "mapTrackingGatesToTrackingGateIds")
    TrackingDto trackingToTrackingDto(Tracking tracking);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackingType", target = "trackingType")
    @Mapping(source = "trackingGateIds", target = "trackingGates", qualifiedByName = "mapTrackingGateIdsToTrackingGates")
    Tracking trackingDtoToTracking(TrackingDto trackingDto);

    @Named("mapTrackingGatesToTrackingGateIds")
    default Set<Long> mapTrackingGatesToTrackingGateIds(Set<TrackingGate> trackingGates) {
        if (trackingGates == null) {
            return null;
        }
        return trackingGates.stream()
                .map(TrackingGate::getId) // Ensure TrackingGate has an ID field
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
                    trackingGate.setId(trackingGateId); // Setting the ID on the entity
                    return trackingGate;
                })
                .collect(Collectors.toSet());
    }
}
