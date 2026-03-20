package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.RequiredAction;
import ma.inwi.msproject.exceptions.RequiredActionNotFoundException;
import ma.inwi.msproject.mappers.RequiredActionMapper;
import ma.inwi.msproject.repositories.DepartementGateProjectRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import ma.inwi.msproject.service.RequiredActionService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequiredActionServiceTest {

    @Mock
    private RequiredActionRepository requiredActionRepository;

    @Mock
    private RequiredActionMapper requiredActionMapper;

    @Mock
    private DepartementGateProjectRepository departementGateProjectRepository;

    @InjectMocks
    private RequiredActionService requiredActionService;

    private RequiredAction requiredAction;
    private RequiredActionDto requiredActionDto;

    private DepartementGateProject departementGateProject;

    private RequiredActionDto updatedRequiredActionDto;

    @BeforeEach
    void setUp() {
        departementGateProject = DepartementGateProject.builder().id(2L).build();

        requiredAction = RequiredAction.builder()
                .id(1L)
                .requiredAction("Action 1")
                .departementGateProject(departementGateProject)
                .build();

        requiredActionDto = RequiredActionDto.builder()
                .id(1L)
                .requiredAction("Action 1")
                .departementGateProjectId(2L)
                .build();

        updatedRequiredActionDto = RequiredActionDto.builder()
                .id(1L)
                .requiredAction("Action 1 (modified)")
                .departementGateProjectId(2L)
                .build();
    }


    @Test
    void testAddRequiredAction() {
        when(requiredActionMapper.requiredActionDtoToRequiredAction(requiredActionDto)).thenReturn(requiredAction);
        when(requiredActionRepository.save(requiredAction)).thenReturn(requiredAction);
        when(requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction)).thenReturn(requiredActionDto);

        RequiredActionDto result = requiredActionService.addActionRequise(requiredActionDto);

        assertNotNull(result);
        assertEquals(requiredActionDto.getId(), result.getId());
        verify(requiredActionRepository, times(1)).save(requiredAction);
    }


    @Test
    void testGetActionRequiseById_Success() {
        when(requiredActionRepository.findById(1L)).thenReturn(Optional.of(requiredAction));
        when(requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction)).thenReturn(requiredActionDto);

        RequiredActionDto result = requiredActionService.getActionRequiseById(1L);

        assertNotNull(result);
        assertEquals(requiredActionDto.getId(), result.getId());
        assertEquals(requiredActionDto.getRequiredAction(), result.getRequiredAction());
        verify(requiredActionRepository, times(1)).findById(1L);
    }


    @Test
    void testGetActionRequiseById_NotFound() {
        when(requiredActionRepository.findById(1L)).thenReturn(Optional.empty());

        RequiredActionNotFoundException exception = assertThrows(RequiredActionNotFoundException.class, () -> {
            requiredActionService.getActionRequiseById(1L);
        });
        assertEquals("Action Requise with 1 was not found !", exception.getMessage());
    }


    @Test
    void testGetAllActionRequises_Success() {
        Set<RequiredAction> requiredActions = new HashSet<>();
        requiredActions.add(requiredAction);

        when(requiredActionRepository.findAll()).thenReturn(new ArrayList<>(requiredActions));
        when(requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction)).thenReturn(requiredActionDto);

        Set<RequiredActionDto> result = requiredActionService.getAllActionRequises();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(requiredActionDto));
        verify(requiredActionRepository, times(1)).findAll();
        verify(requiredActionMapper, times(1)).requiredActiontoRequiredActionDto(requiredAction);
    }


    @Test
    void testUpdateActionRequise_Success() {
        when(requiredActionRepository.findById(1L)).thenReturn(Optional.of(requiredAction));
        when(requiredActionRepository.save(requiredAction)).thenReturn(requiredAction);
        when(requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction)).thenReturn(requiredActionDto);
        when(departementGateProjectRepository.findById(2L)).thenReturn(Optional.of(departementGateProject));

        RequiredActionDto result = requiredActionService.updateActionRequise(1L, updatedRequiredActionDto);

        assertNotNull(result);
        verify(requiredActionRepository, times(1)).save(requiredAction);
    }


    @Test
    void testUpdateActionRequise_NotFound() {
        // Mock repository behavior
        when(requiredActionRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify exception
        RequiredActionNotFoundException exception = assertThrows(RequiredActionNotFoundException.class, () -> {
            requiredActionService.updateActionRequise(1L, updatedRequiredActionDto);
        });
        assertEquals("Action Requise with 1 was not found !", exception.getMessage());
    }


    @Test
    void deleteActionRequise_ShouldDeleteRequiredAction_WhenIdIsValid() {
        // Given
        Long id = 1L;
        RequiredAction requiredAction = new RequiredAction();
        requiredAction.setId(id);

        // Mocking repository methods
        when(requiredActionRepository.findById(id)).thenReturn(Optional.of(requiredAction));
        when(requiredActionRepository.save(any(RequiredAction.class))).thenReturn(requiredAction);

        // Act
        requiredActionService.deleteActionRequise(id);

        // Assert
        assertTrue(requiredAction.isDeleted());

        // Verifications
        verify(requiredActionRepository, times(1)).findById(id);
        verify(requiredActionRepository, times(1)).save(requiredAction);
    }

    @Test
    void deleteActionRequise_ShouldThrowRequiredActionNotFoundException_WhenRequiredActionNotFound() {
        // Given
        Long id = 1L;

        // Mocking repository methods to return empty
        when(requiredActionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RequiredActionNotFoundException exception = assertThrows(RequiredActionNotFoundException.class, () -> {
            requiredActionService.deleteActionRequise(id);
        });

        assertEquals("Action requise with 1 was not found !", exception.getMessage());

        // Verifications
        verify(requiredActionRepository, times(1)).findById(id);
        verify(requiredActionRepository, times(0)).save(any(RequiredAction.class)); // Save should not be called
    }



}