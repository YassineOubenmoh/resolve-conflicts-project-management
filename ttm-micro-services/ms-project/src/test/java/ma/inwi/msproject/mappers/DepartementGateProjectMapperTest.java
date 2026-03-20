package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.GateProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepartementGateProjectMapperTest {

    private DepartementGateProjectMapper departementGateProjectMapper;

    @BeforeEach
    void setUp() {
        departementGateProjectMapper = Mappers.getMapper(DepartementGateProjectMapper.class);
    }

    @Test
    void testDepartementGateToDepartementGateDto() {
        GateProject gateProject = new GateProject();
        gateProject.setId(1L);

        Departement departement = new Departement();
        departement.setId(2L);

        DepartementGateProject departementGateProject = new DepartementGateProject();
        departementGateProject.setId(3L);
        departementGateProject.setGateProject(gateProject);
        departementGateProject.setDepartement(departement);

        DepartementGateProjectDto departementGateProjectDto = departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject);

        assertNotNull(departementGateProjectDto);
        assertEquals(departementGateProject.getId(), departementGateProjectDto.getId());
        assertEquals(departementGateProject.getGateProject().getId(), departementGateProjectDto.getGateProjectId());
        assertEquals(departementGateProject.getDepartement().getId(), departementGateProjectDto.getDepartementId());
    }

    @Test
    void testDepartementGateDtoToDepartementGate() {
        DepartementGateProjectDto departementGateProjectDto = new DepartementGateProjectDto();
        departementGateProjectDto.setId(3L);
        departementGateProjectDto.setGateProjectId(1L);
        departementGateProjectDto.setDepartementId(2L);

        DepartementGateProject departementGateProject = departementGateProjectMapper.departementGateProjectDtoToDepartementGateProject(departementGateProjectDto);

        assertNotNull(departementGateProject);
        assertEquals(departementGateProjectDto.getId(), departementGateProject.getId());
        assertNotNull(departementGateProject.getGateProject());
        assertEquals(departementGateProjectDto.getGateProjectId(), departementGateProject.getGateProject().getId());
        assertNotNull(departementGateProject.getDepartement());
        assertEquals(departementGateProjectDto.getDepartementId(), departementGateProject.getDepartement().getId());
    }


}