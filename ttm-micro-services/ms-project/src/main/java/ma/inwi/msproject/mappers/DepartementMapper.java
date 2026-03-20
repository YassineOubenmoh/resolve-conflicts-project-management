package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.entities.DepartementGateProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DepartementMapper {

    DepartementMapper INSTANCE = Mappers.getMapper(DepartementMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "departement", target = "departement")
    @Mapping(source = "departementGateProjects", target = "departementGateProjectIds", qualifiedByName = "mapDepartementGateProjectsToIds")
    DepartementDto departementToDepartementDto(Departement departement);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "departement", target = "departement")
    @Mapping(source = "departementGateProjectIds", target = "departementGateProjects", qualifiedByName = "mapDepartementGateProjectIdsToEntities")
    Departement departementDtoToDepartement(DepartementDto departementDto);

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
}
