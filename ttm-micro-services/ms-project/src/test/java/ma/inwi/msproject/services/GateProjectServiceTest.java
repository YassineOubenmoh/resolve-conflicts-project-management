package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.enums.DecisionType;
import ma.inwi.msproject.exceptions.GateProjectNotFoundException;
import ma.inwi.msproject.mappers.GateProjectMapper;
import ma.inwi.msproject.repositories.GateProjectRepository;
import ma.inwi.msproject.service.GateProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GateProjectServiceTest {

    @Mock
    private GateProjectRepository gateProjectRepository;

    @Mock
    private GateProjectMapper gateProjectMapper;

    @InjectMocks
    private GateProjectService gateProjectService;

    private GateProjectDto gateProjectDto;
    private GateProject gateProject;

    @BeforeEach
    void setUp() {
        gateProjectDto = new GateProjectDto();
        gateProjectDto.setId(1L);

        gateProject = new GateProject();
        gateProject.setId(1L);
    }

    @Test
    void testAddGateProject() {
        when(gateProjectMapper.gateProjectDtoToGateProject(gateProjectDto)).thenReturn(gateProject);
        when(gateProjectRepository.save(gateProject)).thenReturn(gateProject);
        when(gateProjectMapper.gateProjectToGateProjectDto(gateProject)).thenReturn(gateProjectDto);

        GateProjectDto result = gateProjectService.addGateProject(gateProjectDto);

        assertNotNull(result);
        assertEquals(gateProjectDto.getId(), result.getId());
        verify(gateProjectRepository, times(1)).save(gateProject);
        verify(gateProjectMapper, times(1)).gateProjectDtoToGateProject(gateProjectDto);
        verify(gateProjectMapper, times(1)).gateProjectToGateProjectDto(gateProject);
    }

    @Test
    void testGetGateProjectById() {
        when(gateProjectRepository.findById(1L)).thenReturn(Optional.of(gateProject));
        when(gateProjectMapper.gateProjectToGateProjectDto(gateProject)).thenReturn(gateProjectDto);

        GateProjectDto result = gateProjectService.getGateProjectById(1L);

        assertNotNull(result);
        assertEquals(gateProjectDto.getId(), result.getId());
        verify(gateProjectRepository, times(1)).findById(1L);
        verify(gateProjectMapper, times(1)).gateProjectToGateProjectDto(gateProject);
    }

    /*
    @Test
    void testGetGateProjectById_NotFound() {
        when(gateProjectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(GateProjectNotFoundException.class, () -> gateProjectService.getGateProjectById(1L));

        assertEquals("Gate Project with 1 was not found ! ", exception.getMessage());
        verify(gateProjectRepository, times(1)).findById(1L);
    }

     */

    @Test
    void testUpdateGateProject() {
        when(gateProjectRepository.findById(1L)).thenReturn(Optional.of(gateProject));
        when(gateProjectMapper.gateProjectToGateProjectDto(gateProject)).thenReturn(gateProjectDto);

        gateProjectDto.setInProgress(true);
        gateProjectDto.setDecisionType(DecisionType.GO);
        gateProjectDto.setInformation(Set.of("Info 1", "Info 2"));

        GateProjectDto result = gateProjectService.updateGateProject(1L, gateProjectDto);

        assertNotNull(result);
        assertEquals(gateProjectDto.isInProgress(), result.isInProgress());
        assertEquals(gateProjectDto.getDecisionType(), result.getDecisionType());
        assertEquals(gateProjectDto.getInformation(), result.getInformation());
        verify(gateProjectRepository, times(1)).save(gateProject);
    }

    @Test
    void testUpdateGateProject_NotFound() {
        when(gateProjectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(GateProjectNotFoundException.class, () -> gateProjectService.updateGateProject(1L, gateProjectDto));

        assertEquals("Gate Project with 1 was not found ! ", exception.getMessage());
        verify(gateProjectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetGatesProjectByProjectId() {
        Set<GateProject> gateProjects = new HashSet<>();
        gateProjects.add(gateProject);

        Set<GateProjectDto> gateProjectDtos = new HashSet<>();
        gateProjectDtos.add(gateProjectDto);

        when(gateProjectRepository.findGateProjectsByProjectId(1L)).thenReturn(gateProjects);
        when(gateProjectMapper.gateProjectToGateProjectDto(gateProject)).thenReturn(gateProjectDto);

        Set<GateProjectDto> result = gateProjectService.getGatesProjectByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(gateProjectDto));
        verify(gateProjectRepository, times(1)).findGateProjectsByProjectId(1L);
    }

    @Test
    void deleteGateProject_ShouldDeleteGateProject_WhenIdIsValid() {
        // Given
        Long id = 1L;
        GateProject gateProject = new GateProject();
        gateProject.setId(id);

        // Mocking repository methods
        when(gateProjectRepository.findById(id)).thenReturn(Optional.of(gateProject));
        when(gateProjectRepository.save(any(GateProject.class))).thenReturn(gateProject);

        // Act
        gateProjectService.deleteGateProject(id);

        // Assert
        assertTrue(gateProject.isDeleted());  // Ensure the gateProject is marked as deleted

        verify(gateProjectRepository, times(1)).findById(id);  // Verify the findById is called
        verify(gateProjectRepository, times(1)).save(gateProject);   // Verify that save is called to persist the changes
    }



}
