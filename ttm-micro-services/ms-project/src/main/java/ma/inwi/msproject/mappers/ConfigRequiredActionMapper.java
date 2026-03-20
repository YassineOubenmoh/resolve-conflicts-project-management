package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.ConfigRequiredActionDto;
import ma.inwi.msproject.entities.ConfigRequiredAction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigRequiredActionMapper {

    ConfigRequiredActionDto configRequiredActionToConfigRequiredActionDto(ConfigRequiredAction entity);

    ConfigRequiredAction configRequiredActionDtoToConfigRequiredAction(ConfigRequiredActionDto dto);
}
