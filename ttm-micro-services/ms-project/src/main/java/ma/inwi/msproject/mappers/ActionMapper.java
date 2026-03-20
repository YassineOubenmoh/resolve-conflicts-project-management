package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.entities.Action;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    @Mapping(source = "responseDocument", target = "responseDocument")
    @Mapping(source = "actionDocument", target = "actionDocument")
    @Mapping(source = "requiredAction.id", target = "requiredActionId")
    @Mapping(source = "actionCreatedBy", target = "actionCreatedBy")
    @Mapping(source = "validatedBy", target = "validatedBy")
    @Mapping(source = "impactSenderEmail", target = "impactSenderEmail")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "lastModifiedAt", target = "lastModifiedAt")
    ActionDto actionToActionDto(Action action);

    @Mapping(source = "responseDocument", target = "responseDocument")
    @Mapping(source = "actionDocument", target = "actionDocument")
    @Mapping(source = "requiredActionId", target = "requiredAction.id")
    @Mapping(source = "actionCreatedBy", target = "actionCreatedBy")
    @Mapping(source = "validatedBy", target = "validatedBy")
    @Mapping(source = "impactSenderEmail", target = "impactSenderEmail")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "lastModifiedAt", target = "lastModifiedAt")
    Action actionDtoToAction(ActionDto actionDto);
}
