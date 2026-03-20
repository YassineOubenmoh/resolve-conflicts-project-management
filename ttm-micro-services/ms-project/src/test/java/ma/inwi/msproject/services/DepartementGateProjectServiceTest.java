package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.exceptions.DepartementGateProjectNotFoundException;
import ma.inwi.msproject.exceptions.DepartementNotFoundException;
import ma.inwi.msproject.exceptions.GateProjectNotFoundException;
import ma.inwi.msproject.mappers.DepartementGateProjectMapper;
import ma.inwi.msproject.repositories.DepartementGateProjectRepository;
import ma.inwi.msproject.repositories.DepartementRepository;
import ma.inwi.msproject.repositories.GateProjectRepository;
import ma.inwi.msproject.service.DepartementGateProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementGateProjectServiceTest {

    @Mock
    private DepartementGateProjectRepository departementGateProjectRepository;

    @Mock
    private DepartementGateProjectMapper departementGateProjectMapper;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private GateProjectRepository gateProjectRepository;

    @InjectMocks
    private DepartementGateProjectService departementGateProjectService;

    private DepartementGateProject departementGateProject;
    private DepartementGateProjectDto departementGateProjectDto;
    private DepartementGateProjectDto updatedDepartementGateProjectDto;

    private GateProject gateProject;
    private GateProject updateGateProject;

    private Departement departement;
    private Departement updateDepartement;


    @BeforeEach
    void setUp(){
        gateProject = GateProject.builder().id(2L).build();
        updateGateProject = GateProject.builder().id(6L).build();
        updateDepartement = Departement.builder().id(9L).build();

        departement = Departement.builder().id(3L).build();

        departementGateProject = DepartementGateProject.builder()
                .id(1L)
                .gateProject(gateProject)
                .departement(departement)
                .build();

        departementGateProjectDto = DepartementGateProjectDto.builder()
                .id(1L)
                .gateProjectId(2L)
                .departementId(3L)
                .build();

        updatedDepartementGateProjectDto = DepartementGateProjectDto.builder()
                .id(1L)
                .gateProjectId(6L)
                .departementId(9L)
                .build();
    }


    /*
    @Test
    void testAffectGateProjectToDepartement_Success() {
        when(gateProjectRepository.findById(2L)).thenReturn(Optional.of(gateProject));
        when(departementRepository.findById(3L)).thenReturn(Optional.of(departement));
        when(departementGateProjectRepository.save(any(DepartementGateProject.class))).thenReturn(departementGateProject);
        when(departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(any(DepartementGateProject.class)))
                .thenReturn(departementGateProjectDto);

        DepartementGateProjectDto result = departementGateProjectService.affectGateProjectToDepartement(2L, 3L, "yassine@example.com");

        assertNotNull(result);
        assertEquals(departementGateProjectDto.getId(), result.getId());
        assertEquals(departementGateProjectDto.getGateProjectId(), result.getGateProjectId());
        assertEquals(departementGateProjectDto.getDepartementId(), result.getDepartementId());

        verify(gateProjectRepository, times(1)).findById(2L);
        verify(departementRepository, times(1)).findById(3L);
        verify(departementGateProjectRepository, times(1)).save(any(DepartementGateProject.class));
    }

     */

    @Test
    void testAffectGateProjectToDepartement_GateProjectNotFound() {
        when(gateProjectRepository.findById(2L)).thenReturn(Optional.empty());

        GateProjectNotFoundException exception = assertThrows(GateProjectNotFoundException.class, () ->
                departementGateProjectService.affectGateProjectToDepartement(2L, 3L, "yassine@example.com"));

        assertEquals("The gate of project with the id 2 was not found !", exception.getMessage());

        verify(gateProjectRepository, times(1)).findById(2L);
        verify(departementRepository, never()).findById(anyLong());
        verify(departementGateProjectRepository, never()).save(any(DepartementGateProject.class));
    }

    @Test
    void testAffectGateProjectToDepartement_DepartementNotFound() {
        when(gateProjectRepository.findById(2L)).thenReturn(Optional.of(gateProject));
        when(departementRepository.findById(3L)).thenReturn(Optional.empty());

        DepartementNotFoundException exception = assertThrows(DepartementNotFoundException.class, () ->
                departementGateProjectService.affectGateProjectToDepartement(2L, 3L, "yassine@example.com"));

        assertEquals("The departement with id 3 was not found !", exception.getMessage());

        verify(gateProjectRepository, times(1)).findById(2L);
        verify(departementRepository, times(1)).findById(3L);
        verify(departementGateProjectRepository, never()).save(any(DepartementGateProject.class));
    }


    @Test
    void testGetDepartementGateProjectById_Success() {
        // Mock repository behavior
        when(departementGateProjectRepository.findById(1L)).thenReturn(Optional.of(departementGateProject));
        when(departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject)).thenReturn(departementGateProjectDto);
        DepartementGateProjectDto result = departementGateProjectService.getDepartementGateProjectAffectationById(1L);

        assertNotNull(result);
        assertEquals(departementGateProjectDto.getId(), result.getId());
        assertEquals(departementGateProjectDto.getDepartementId(), result.getDepartementId());
        verify(departementGateProjectRepository, times(1)).findById(1L);
    }


    @Test
    void testGetDepartementGateProjectById_NotFound() {
        when(departementGateProjectRepository.findById(1L)).thenReturn(Optional.empty());

        DepartementGateProjectNotFoundException exception = assertThrows(DepartementGateProjectNotFoundException.class, () -> {
            departementGateProjectService.getDepartementGateProjectAffectationById(1L);
        });
        assertEquals("Departement GateProject with 1 was not found !", exception.getMessage());
    }


    @Test
    void testGetAllDepartementGates_Success() {
        Set<DepartementGateProject> departementGateProjects = new HashSet<>();
        departementGateProjects.add(departementGateProject);

        when(departementGateProjectRepository.findAll()).thenReturn(new ArrayList<>(departementGateProjects));
        when(departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject)).thenReturn(departementGateProjectDto);

        Set<DepartementGateProjectDto> result = departementGateProjectService.getAllProjectGatesAffectationsToDepartement();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(departementGateProjectDto));
        verify(departementGateProjectRepository, times(1)).findAll();
        verify(departementGateProjectMapper, times(1)).departementGateProjectToDepartementGateProjectDto(departementGateProject);
    }


    @Test
    void testUpdateDepartementGate_Success() {
        when(departementGateProjectRepository.findById(1L)).thenReturn(Optional.of(departementGateProject));
        when(gateProjectRepository.findById(6L)).thenReturn(Optional.of(updateGateProject));
        when(departementRepository.findById(9L)).thenReturn(Optional.of(updateDepartement));
        when(departementGateProjectRepository.save(departementGateProject)).thenReturn(departementGateProject);
        when(departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject)).thenReturn(departementGateProjectDto);

        DepartementGateProjectDto result = departementGateProjectService.updateGateProjectAffectationToDepartement(1L, updatedDepartementGateProjectDto);

        assertNotNull(result);
        verify(departementGateProjectRepository, times(1)).save(departementGateProject);
    }


    @Test
    void testUpdateDepartementGate_NotFound() {
        when(departementGateProjectRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify exception
        DepartementGateProjectNotFoundException exception = assertThrows(DepartementGateProjectNotFoundException.class, () -> {
            departementGateProjectService.updateGateProjectAffectationToDepartement(1L, updatedDepartementGateProjectDto);
        });

        assertEquals("Departement gate with 1 was not found !", exception.getMessage());
    }



    @Test
    void deleteGateProjectAffectationToDepartement_ShouldDeleteGateProject_WhenIdIsValid() {
        // Given
        Long id = 1L;
        DepartementGateProject departementGateProject = new DepartementGateProject();
        departementGateProject.setId(id);

        // Mocking repository methods
        when(departementGateProjectRepository.findById(id)).thenReturn(Optional.of(departementGateProject));
        when(departementGateProjectRepository.save(any(DepartementGateProject.class))).thenReturn(departementGateProject);

        // Act
        departementGateProjectService.deleteGateProjectAffectationToDepartement(id);

        // Assert
        assertTrue(departementGateProject.isDeleted());  // Ensure the departementGateProject is marked as deleted

        // Verifications
        verify(departementGateProjectRepository, times(1)).findById(id);  // Verify findById is called
        verify(departementGateProjectRepository, times(1)).save(departementGateProject);  // Verify save is called
    }

    @Test
    void deleteGateProjectAffectationToDepartement_ShouldThrowDepartementGateProjectNotFoundException_WhenDepartementGateProjectNotFound() {
        // Given
        Long id = 1L;

        // Mocking repository methods to return empty
        when(departementGateProjectRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        DepartementGateProjectNotFoundException exception = assertThrows(DepartementGateProjectNotFoundException.class, () -> {
            departementGateProjectService.deleteGateProjectAffectationToDepartement(id);
        });

        assertEquals("Departement Gate with 1 was not found !", exception.getMessage());

        // Verifications
        verify(departementGateProjectRepository, times(1)).findById(id);   // Verify findById is called once
        verify(departementGateProjectRepository, times(0)).save(any(DepartementGateProject.class));  // Verify save is not called
    }



}