package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.RequiredAction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequiredActionMapperTest {

    private final RequiredActionMapper mapper = Mappers.getMapper(RequiredActionMapper.class);

    @Test
    void testRequiredActionToRequiredActionDto() {
        DepartementGateProject departementGateProject = DepartementGateProject.builder().id(1L).build();
        RequiredAction requiredAction = RequiredAction.builder()
                .id(1L)
                .requiredAction("Test Action")
                .departementGateProject(departementGateProject)
                .build();

        RequiredActionDto dto = mapper.requiredActiontoRequiredActionDto(requiredAction);

        assertNotNull(dto);
        assertEquals(requiredAction.getId(), dto.getId());
        assertEquals(requiredAction.getRequiredAction(), dto.getRequiredAction());
        assertEquals(requiredAction.getDepartementGateProject().getId(), dto.getDepartementGateProjectId());
    }

    @Test
    void testActionRequiseDtoToActionRequise() {
        RequiredActionDto dto = RequiredActionDto.builder()
                .id(1L)
                .requiredAction("Test Action")
                .departementGateProjectId(1L)
                .build();

        RequiredAction entity = mapper.requiredActionDtoToRequiredAction(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getRequiredAction(), entity.getRequiredAction());
        assertNotNull(entity.getDepartementGateProject());
        assertEquals(dto.getDepartementGateProjectId(), entity.getDepartementGateProject().getId());
    }


}