package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.dto.GateProjectResponseDto;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.GateType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GateProjectMapper {

    GateProjectMapper INSTANCE = Mappers.getMapper(GateProjectMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackingGate.id", target = "trackingGateId")
    @Mapping(source = "currentGate", target = "currentGate")
    @Mapping(source = "passingDate", target = "passingDate")
    @Mapping(source = "inProgress", target = "inProgress")
    @Mapping(source = "startingGate", target = "startingGate")
    @Mapping(source = "decisionType", target = "decisionType")
    @Mapping(source = "information", target = "information")
    @Mapping(source = "actions", target = "actions")
    @Mapping(source = "decisions", target = "decisions")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "departementGateProjects", target = "departementGateProjectIds", qualifiedByName = "mapDepartementGateProjectsToIds")
    @Mapping(source = "projectCompleted", target = "projectCompleted")
    GateProjectDto gateProjectToGateProjectDto(GateProject gateProject);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackingGateId", target = "trackingGate.id")
    @Mapping(source = "currentGate", target = "currentGate")
    @Mapping(source = "passingDate", target = "passingDate")
    @Mapping(source = "inProgress", target = "inProgress")
    @Mapping(source = "startingGate", target = "startingGate")
    @Mapping(source = "decisionType", target = "decisionType")
    @Mapping(source = "information", target = "information")
    @Mapping(source = "actions", target = "actions")
    @Mapping(source = "decisions", target = "decisions")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "departementGateProjectIds", target = "departementGateProjects", qualifiedByName = "mapDepartementGateProjectIdsToEntities")
    @Mapping(source = "projectCompleted", target = "projectCompleted")
    GateProject gateProjectDtoToGateProject(GateProjectDto gateProjectDto);

    @Named("mapDepartementGateProjectsToIds")
    default Set<Long> mapDepartementGateProjectsToIds(Set<DepartementGateProject> departementGateProjects) {
        if (departementGateProjects == null) {
            return null;
        }
        return departementGateProjects.stream()
                .map(DepartementGateProject::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapDepartementGateProjectIdsToEntities")
    default Set<DepartementGateProject> mapDepartementGateProjectIdsToEntities(Set<Long> departementGateProjectIds) {
        if (departementGateProjectIds == null) {
            return null;
        }
        return departementGateProjectIds.stream()
                .map(id -> {
                    DepartementGateProject departementGateProject = new DepartementGateProject();
                    departementGateProject.setId(id);
                    return departementGateProject;
                })
                .collect(Collectors.toSet());
    }



    //Mapping GateProject to GateProjectResponseDto
    @Mapping(source = "decisionType", target = "decisionType")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "departementGateProjects", target = "departementGateProjectIds", qualifiedByName = "mapDepartementGateProjectsToIds")
    @Mapping(source = "trackingGate", target = "gate", qualifiedByName = "mapTrackingGateToGate")
    @Mapping(source = "projectCompleted", target = "projectCompleted")
    GateProjectResponseDto gateProjectToGateProjectResponseDto(GateProject gateProject);

    @Mapping(source = "decisionType", target = "decisionType")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "departementGateProjectIds", target = "departementGateProjects", qualifiedByName = "mapDepartementGateProjectIdsToEntities")
    @Mapping(source = "gate", target = "trackingGate", qualifiedByName = "mapGateToTrackingGate")
    @Mapping(source = "projectCompleted", target = "projectCompleted")
    GateProject gateProjectResponseDtoToGateProject(GateProjectResponseDto gateProjectResponseDto);

    @Named("mapTrackingGateToGate")
    default GateType mapTrackingGateToGate(TrackingGate trackingGate) {
        if (trackingGate == null || trackingGate.getGate() == null) {
            return null;
        }
        return trackingGate.getGate().getGateType();
    }

    @Named("mapGateToTrackingGate")
    default TrackingGate mapGateToTrackingGate(GateType gateType) {
        if (gateType == null) {
            return null;
        }

        Gate gate = new Gate();
        gate.setGateType(gateType);

        TrackingGate trackingGate = new TrackingGate();
        trackingGate.setGate(gate);

        return trackingGate;
    }

}
