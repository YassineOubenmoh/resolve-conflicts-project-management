package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.RequiredAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DepartementGateProjectMapper {

    DepartementGateProjectMapper INSTANCE = Mappers.getMapper(DepartementGateProjectMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "gateProject.id", target = "gateProjectId")
    @Mapping(source = "departement.id", target = "departementId")
    @Mapping(source = "requiredActions", target = "requiredActionLabels", qualifiedByName = "mapRequiredActionsToLabels")
    DepartementGateProjectDto departementGateProjectToDepartementGateProjectDto(DepartementGateProject departementGateProject);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "gateProjectId", target = "gateProject.id")
    @Mapping(source = "departementId", target = "departement.id")
    @Mapping(source = "requiredActionLabels", target = "requiredActions", qualifiedByName = "mapLabelsToRequiredActions")
    DepartementGateProject departementGateProjectDtoToDepartementGateProject(DepartementGateProjectDto departementGateProjectDto);

    @Named("mapRequiredActionsToLabels")
    default Set<String> mapRequiredActionsToLabels(Set<RequiredAction> requiredActions) {
        if (requiredActions == null) {
            return null;
        }
        return requiredActions.stream()
                .map(RequiredAction::getRequiredAction)
                .collect(Collectors.toSet());
    }


    @Named("mapLabelsToRequiredActions")
    default Set<RequiredAction> mapLabelsToRequiredActions(Set<String> labels) {
        if (labels == null) {
            return null;
        }
        return labels.stream()
                .map(label -> {
                    RequiredAction requiredAction = new RequiredAction();
                    requiredAction.setRequiredAction(label);
                    return requiredAction;
                })
                .collect(Collectors.toSet());
    }
}
