package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.TrackingGate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrackingGateMapper {

    @Mapping(source = "gate.id", target = "gateId")
    @Mapping(source = "tracking.id", target = "trackingId")
    @Mapping(source = "gateBefore.id", target = "gateBeforeId")
    @Mapping(source = "gateAfter.id", target = "gateAfterId")
    @Mapping(target = "gateProjectIds", source = "gateProjects", qualifiedByName = "mapGateProjectsToIds")
    TrackingGateDto trackingGateToTrackingGateDto(TrackingGate trackingGate);

    @Mapping(source = "gateId", target = "gate.id")
    @Mapping(source = "trackingId", target = "tracking.id")
    @Mapping(source = "gateBeforeId", target = "gateBefore.id")
    @Mapping(source = "gateAfterId", target = "gateAfter.id")
    @Mapping(target = "gateProjects", source = "gateProjectIds", qualifiedByName = "mapIdsToGateProjects") // Handling manually if needed
    TrackingGate trackingGateDtoToTrackingGate(TrackingGateDto trackingGateDto);

    @Named("mapGateProjectsToIds")
    default Set<Long> mapGateProjectsToIds(Set<GateProject> gateProjects) {
        if (gateProjects == null){
            return null;
        }
        return gateProjects.stream()
                .map(GateProject::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapIdsToGateProjects")
    default Set<GateProject> mapIdsToGateProjects(Set<Long> gateProjectIds) {
        if (gateProjectIds == null){
            return null;
        }
        return gateProjectIds.stream()
                .map(id -> {
                    GateProject gateProject = new GateProject();
                    gateProject.setId(id); // Assuming GateProject has a setId method
                    return gateProject;
                })
                .collect(Collectors.toSet());
    }
}
