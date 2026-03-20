package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.enums.GateType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(source = "gateProjects", target = "gateProjectIds", qualifiedByName = "gateProjectToIds")
    @Mapping(target = "currentGate", expression = "java(getCurrentGateProject(project))")
    @Mapping(target = "ownerUsername", source = "ownerUsername")
    @Mapping(target = "ownerFullName", source = "ownerFullName")
    ProjectDto projectToProjectDto(Project project);

    @Mapping(source = "gateProjectIds", target = "gateProjects", qualifiedByName = "idsToGateProject")
    @Mapping(target = "ownerUsername", source = "ownerUsername")
    @Mapping(target = "ownerFullName", source = "ownerFullName")
    Project projectDtoToProject(ProjectDto projectDto);

    /*
    @Mapping(source = "gateProjects", target = "gateProjectIds", qualifiedByName = "gateProjectToIds")
    @Mapping(target = "currentGate", expression = "java(getCurrentGateProject(project))")
    ProjectResponseDto projectToProjectResponse(Project project);

     */



    @Named("idsToGateProject")
    default Set<GateProject> idsToGateProject(Set<Long> gateProjectIds){
        if (gateProjectIds == null){
            return null;
        }
        return gateProjectIds.stream()
                .map(gateProjectId -> {
                    GateProject gateProject = new GateProject();
                    gateProject.setId(gateProjectId);
                    return gateProject;
                })
                .collect(Collectors.toSet());
    }

    @Named("gateProjectToIds")
    default Set<Long> gateProjectToIds(Set<GateProject> gateProjects){
        if (gateProjects == null){
            return null;
        }
        return gateProjects.stream()
                .map(GateProject::getId)
                .collect(Collectors.toSet());
    }

    default GateType getCurrentGateProject(Project project){
        if (project.getGateProjects() == null){
            return null;
        }
        for (GateProject gateProject : project.getGateProjects()){
            if (gateProject.isCurrentGate()){
                return gateProject.getTrackingGate().getGate().getGateType();
            }
        }
        return null;
    }

}
