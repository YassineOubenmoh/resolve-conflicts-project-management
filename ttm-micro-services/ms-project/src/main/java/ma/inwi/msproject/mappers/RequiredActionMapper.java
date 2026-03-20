package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.entities.RequiredAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RequiredActionMapper {

    RequiredActionMapper INSTANCE = Mappers.getMapper(RequiredActionMapper.class);

    @Mapping(source = "departementGateProject.id", target = "departementGateProjectId")
    RequiredActionDto requiredActiontoRequiredActionDto(RequiredAction requiredAction);

    @Mapping(source = "departementGateProjectId", target = "departementGateProject.id")
    RequiredAction requiredActionDtoToRequiredAction(RequiredActionDto requiredActionDto);
}
