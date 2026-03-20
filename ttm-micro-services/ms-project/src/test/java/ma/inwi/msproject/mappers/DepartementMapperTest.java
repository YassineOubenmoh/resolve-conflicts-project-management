package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.entities.Departement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepartementMapperTest {

    private DepartementMapper departementMapper;

    @BeforeEach
    void setUp() {
        departementMapper = Mappers.getMapper(DepartementMapper.class);
    }

    @Test
    void testDepartementToDepartementDto() {
        Departement departement = new Departement();
        departement.setId(1L);
        departement.setDepartement("IT");

        DepartementDto departementDto = departementMapper.departementToDepartementDto(departement);

        assertNotNull(departementDto);
        assertEquals(departement.getId(), departementDto.getId());
        assertEquals(departement.getDepartement(), departementDto.getDepartement());
    }

    @Test
    void testDepartementDtoToDepartement() {
        DepartementDto departementDto = new DepartementDto();
        departementDto.setId(1L);
        departementDto.setDepartement("HR");

        Departement departement = departementMapper.departementDtoToDepartement(departementDto);

        assertNotNull(departement);
        assertEquals(departementDto.getId(), departement.getId());
        assertEquals(departementDto.getDepartement(), departement.getDepartement());
    }


}