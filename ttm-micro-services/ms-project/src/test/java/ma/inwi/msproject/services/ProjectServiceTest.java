package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.enums.DecisionType;
import ma.inwi.msproject.exceptions.ProjectNotFoundException;
import ma.inwi.msproject.exceptions.ReachedFinalGateException;
import ma.inwi.msproject.exceptions.UnauthorizedPassageToNextGateException;
import ma.inwi.msproject.mappers.ProjectMapper;
import ma.inwi.msproject.mappers.TrackingGateMapper;
import ma.inwi.msproject.repositories.GateProjectRepository;
import ma.inwi.msproject.repositories.ProjectRepository;
import ma.inwi.msproject.repositories.TrackingRepository;
import ma.inwi.msproject.service.ProjectService;
import ma.inwi.msproject.service.TrackingGateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private GateProjectRepository gateProjectRepository;

    @Mock
    private TrackingGateService trackingGateService;

    @Mock
    private TrackingGateMapper trackingGateMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;
    private Tracking tracking;
    private TrackingGate trackingGate1, trackingGate2;
    private TrackingGateDto trackingGateDto1, trackingGateDto2;
    private Gate gateBefore;
    private GateProject gateProject1;
    private GateProject gateProject2;

    private Set<GateProject> savedGatesProject = new HashSet<>();
    @BeforeEach
    void setUp() {
        tracking = new Tracking();
        tracking.setId(1L);

        gateBefore = new Gate();
        gateBefore.setId(null);

        trackingGate1 = new TrackingGate();
        trackingGate1.setId(10L);
        trackingGate1.setGateBefore(gateBefore);

        trackingGate2 = new TrackingGate();
        trackingGate2.setId(20L);
        trackingGate2.setGateBefore(new Gate());

        trackingGateDto1 = new TrackingGateDto();
        trackingGateDto1.setId(10L);

        trackingGateDto2 = new TrackingGateDto();
        trackingGateDto2.setId(20L);

        projectDto = new ProjectDto();
        projectDto.setTrackingId(1L);

        project = new Project();
        project.setId(100L);
        project.setDateCreation(LocalDateTime.now());

        projectDto = new ProjectDto();
        projectDto.setId(200L);
        projectDto.setDateCreation(LocalDateTime.now());

        gateProject1 = new GateProject();
        gateProject1.setId(11L);
        gateProject1.setTrackingGate(trackingGate1);
        gateProject1.setProject(project);

        gateProject2 = new GateProject();
        gateProject2.setId(22L);
        gateProject2.setTrackingGate(trackingGate2);
        gateProject2.setProject(project);

        savedGatesProject.add(gateProject1);
        savedGatesProject.add(gateProject2);


    }

    /*
    @Test
    void testAddProjectSuccessfully() {
        projectDto.setTrackingId(1L);

        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        when(trackingGateService.getRelatedGatesToTracking(1L))
                .thenReturn(Set.of(trackingGateDto1, trackingGateDto2));
        when(trackingGateMapper.trackingGateDtoToTrackingGate(trackingGateDto1)).thenReturn(trackingGate1);
        when(trackingGateMapper.trackingGateDtoToTrackingGate(trackingGateDto2)).thenReturn(trackingGate2);
        when(projectMapper.projectDtoToProject(projectDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.projectToProjectDto(project)).thenReturn(projectDto);
        when(gateProjectRepository.save(any(GateProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDto result = projectService.addProject(projectDto);

        assertNotNull(result);
        assertEquals(projectDto, result);

        verify(trackingRepository, times(1)).findById(1L);
        verify(trackingGateService, times(1)).getRelatedGatesToTracking(1L);
        verify(projectRepository, times(2)).save(any(Project.class));

        assertEquals(2, savedGatesProject.size());
    }

     */


    /*
    @Test
    void testGetProjectById_Success() {
        // Given
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(true); // Simulating a non-deleted project
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.projectToProjectDto(project)).thenReturn(projectDto);

        // When
        ProjectDto result = projectService.getProjectById(projectId);

        // Then
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).projectToProjectDto(project);
    }

     */


    /*
    @Test
    void testGetProjectById_NotFound() {
        // Given
        Long projectId = 2L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        });

        assertEquals("Project with 2 was not found ! ", exception.getMessage());
        verify(projectRepository, times(1)).findById(projectId);
        verifyNoInteractions(projectMapper); // Ensure mapper is not called when project is not found
    }

     */


    /*
    @Test
    void testGetProjectById_DeletedProject() {
        // Given
        Long projectId = 3L;
        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(false); // Simulating a deleted project

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When
        ProjectDto result = projectService.getProjectById(projectId);

        // Then
        assertNull(result);
        verify(projectRepository, times(1)).findById(projectId);
        verifyNoInteractions(projectMapper); // Mapper should not be called if project is deleted
    }


     */


    @Test
    void testGetAllProjects_Success() {
        // Given
        Project project1 = new Project();
        project1.setId(1L);
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setDeleted(false);

        Project project3 = new Project();
        project3.setId(3L);
        project3.setDeleted(true); // This project should be filtered out

        List<Project> projects = List.of(project1, project2, project3);

        ProjectDto projectDto1 = new ProjectDto();
        projectDto1.setId(1L);

        ProjectDto projectDto2 = new ProjectDto();
        projectDto2.setId(2L);

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.projectToProjectDto(project1)).thenReturn(projectDto1);
        when(projectMapper.projectToProjectDto(project2)).thenReturn(projectDto2);

        // When
        Set<ProjectDto> result = projectService.getAllProjects();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(projectDto1));
        assertTrue(result.contains(projectDto2));

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).projectToProjectDto(project1);
        verify(projectMapper, times(1)).projectToProjectDto(project2);
        verifyNoMoreInteractions(projectMapper);
    }

    @Test
    void testGetAllProjects_NoProjectsAvailable() {
        // Given
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        Set<ProjectDto> result = projectService.getAllProjects();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectRepository, times(1)).findAll();
        verifyNoInteractions(projectMapper);
    }

    @Test
    void testGetAllProjects_AllProjectsDeleted() {
        // Given
        Project project1 = new Project();
        project1.setId(1L);
        project1.setDeleted(true);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setDeleted(true);

        List<Project> projects = List.of(project1, project2);

        when(projectRepository.findAll()).thenReturn(projects);

        // When
        Set<ProjectDto> result = projectService.getAllProjects();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectRepository, times(1)).findAll();
        verifyNoInteractions(projectMapper);
    }



    @Test
    void testUpdateProject_Success() {
        // Given
        Long projectId = 1L;

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setDeleted(false);

        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setTitle("Updated Title");
        updatedProjectDto.setDescription("Updated Description");
        updatedProjectDto.setMarketType("New Market");
        updatedProjectDto.setProjectType("New Type");
        updatedProjectDto.setTtmComitteeSubCategory("New Subcategory");
        updatedProjectDto.setSubcategoryCommercialCodir("New Commercial Codir");
        updatedProjectDto.setIsConfidential(true);
        updatedProjectDto.setComments(Set.of("Updated Comments", "Update"));
        updatedProjectDto.setDateCreation(LocalDateTime.now());
        updatedProjectDto.setMoas(new HashSet<>());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);
        when(projectMapper.projectToProjectDto(existingProject)).thenReturn(updatedProjectDto);

        // When
        ProjectDto result = projectService.updateProject(projectId, updatedProjectDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("New Market", result.getMarketType());
        assertEquals("New Type", result.getProjectType());
        assertEquals("New Subcategory", result.getTtmComitteeSubCategory());
        assertEquals("New Commercial Codir", result.getSubcategoryCommercialCodir());
        assertTrue(result.getIsConfidential());
        assertEquals(Set.of("Updated Comments", "Update"), result.getComments());
        assertEquals(0, result.getMoas().size());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).projectToProjectDto(existingProject);
    }

    @Test
    void testUpdateProject_ProjectNotFound() {
        // Given
        Long projectId = 1L;
        ProjectDto updatedProjectDto = new ProjectDto();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(projectId, updatedProjectDto));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
        verifyNoInteractions(projectMapper);
    }

    @Test
    void testUpdateProject_ProjectIsDeleted() {
        // Given
        Long projectId = 1L;

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setDeleted(true);

        ProjectDto updatedProjectDto = new ProjectDto();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // When
        ProjectDto result = projectService.updateProject(projectId, updatedProjectDto);

        // Then
        assertNull(result);

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
        verifyNoInteractions(projectMapper);
    }

    /*
    @Test
    void testGoToNextGateProject_ProjectNotFound() {
        // Given
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.goToNextGateProject(projectId));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

     */

    /*
    @Test
    void testGoToNextGateProject_ProjectIsDeleted() {
        // Given
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(true);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When
        projectService.goToNextGateProject(projectId);

        // Then
        // No state change should happen, and no project is saved
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

     */


    /*
    @Test
    void testGoToNextGateProject_UnauthorizedPassage() {
        // Given
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(false);

        Gate gateBefore = new Gate();
        Gate gateAfter = new Gate();

        TrackingGate trackingGateBefore = new TrackingGate();
        trackingGateBefore.setGate(gateBefore);
        TrackingGate trackingGateAfter = new TrackingGate();
        trackingGateAfter.setGate(gateAfter);

        GateProject gateProject1 = new GateProject();
        gateProject1.setCurrentGate(true);
        gateProject1.setTrackingGate(trackingGateBefore);
        gateProject1.setDecisionType(DecisionType.NO_GO);

        GateProject gateProject2 = new GateProject();
        gateProject2.setCurrentGate(false);
        gateProject2.setTrackingGate(trackingGateAfter);

        project.setGateProjects(new HashSet<>(Arrays.asList(gateProject1, gateProject2)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When & Then
        assertThrows(UnauthorizedPassageToNextGateException.class, () -> projectService.goToNextGateProject(projectId));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }
     */



    /*
    @Test
    void testGoToNextGateProject_ReachedFinalGate() {
        // Given
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(false);

        Gate gateBefore = new Gate();

        TrackingGate trackingGateBefore = new TrackingGate();
        trackingGateBefore.setGate(gateBefore);
        trackingGateBefore.setGateAfter(null); // No gate after, indicating it's the final gate

        GateProject gateProject1 = new GateProject();
        gateProject1.setCurrentGate(true);
        gateProject1.setTrackingGate(trackingGateBefore);
        gateProject1.setDecisionType(DecisionType.GO);

        project.setGateProjects(new HashSet<>(Collections.singletonList(gateProject1)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When & Then
        assertThrows(ReachedFinalGateException.class, () -> projectService.goToNextGateProject(projectId));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }
     */


    /*
    @Test
    void testDeleteProject_Success() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setDeleted(false);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        // When
        projectService.deleteProject(projectId);

        // Then
        assertTrue(project.isDeleted());  // Ensure that the project is marked as deleted
        verify(projectRepository, times(1)).save(project);  // Ensure save was called once
    }

     */


    /*
    @Test
    void testDeleteProject_NotFound() {
        // Given
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.deleteProject(projectId);
        });

        // Ensure the exception message matches
        assertEquals("Project with 1 was not found !", exception.getMessage());

        // Verify no save operation was performed
        verify(projectRepository, never()).save(any(Project.class));
    }

     */


}
