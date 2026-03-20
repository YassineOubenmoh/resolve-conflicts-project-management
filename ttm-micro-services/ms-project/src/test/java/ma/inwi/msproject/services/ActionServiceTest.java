package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.entities.Action;
import ma.inwi.msproject.enums.ValidationStatus;
import ma.inwi.msproject.exceptions.ActionNotFoundException;
import ma.inwi.msproject.mappers.ActionMapper;
import ma.inwi.msproject.repositories.ActionRepository;
import ma.inwi.msproject.service.ActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private ActionMapper actionMapper;

    @InjectMocks
    private ActionService actionService;

    private Action action;
    private ActionDto actionDto;

    private Action action2;
    private ActionDto actionDto2;

    @BeforeEach
    void setUp(){
        action = new Action();
        action.setId(1L);
        action.setActionCreatedBy("10L");
        action.setComments(Set.of("Comment 1", "Comment 2"));
        action.setValidationStatus(ValidationStatus.ACCEPTER);

        actionDto = new ActionDto();
        actionDto.setId(1L);
        actionDto.setActionCreatedBy("10L");
        actionDto.setComments(Set.of("Comment 1", "Comment 2"));
        actionDto.setValidationStatus(ValidationStatus.ACCEPTER);

        action2 = new Action();
        action2.setId(2L);
        action2.setDeleted(true);

        actionDto2 = new ActionDto();
        actionDto2.setId(2L);
    }

    /*
    @Test
    void addAction_ShouldReturnActionDto_WhenActionDtoIsValid() {
        when(actionMapper.actionDtoToAction(actionDto)).thenReturn(action);
        when(actionRepository.save(action)).thenReturn(action);
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);

        ActionDto result = actionService.addAction(actionDto);

        assertNotNull(result);
        assertEquals(actionDto.getId(), result.getId());

        verify(actionMapper, times(1)).actionDtoToAction(actionDto);
        verify(actionRepository, times(1)).save(action);
        verify(actionMapper, times(1)).actionToActionDto(action);
    }

     */

    /*
    @Test
    void addAction_ShouldCallMapperAndRepository_WhenActionDtoIsValid() {
        when(actionMapper.actionDtoToAction(actionDto)).thenReturn(action);
        when(actionRepository.save(action)).thenReturn(action);
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);

        actionService.addAction(actionDto);

        verify(actionMapper, times(1)).actionDtoToAction(actionDto);
        verify(actionRepository, times(1)).save(action);
        verify(actionMapper, times(1)).actionToActionDto(action);
    }

     */


    @Test
    void getActionById_ShouldReturnActionDto_WhenActionIsFoundAndNotDeleted() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);

        ActionDto result = actionService.getActionById(1L);

        assertNotNull(result);
        assertEquals(actionDto.getId(), result.getId());

        verify(actionRepository, times(1)).findById(1L);
        verify(actionMapper, times(1)).actionToActionDto(action);
    }

    @Test
    void getActionById_ShouldThrowException_WhenActionIsNotFound() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> {
            actionService.getActionById(1L);
        });

        verify(actionRepository, times(1)).findById(1L);
    }

    @Test
    void getActionById_ShouldReturnNull_WhenActionIsFoundButDeleted() {
        action.setDeleted(true);
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));

        ActionDto result = actionService.getActionById(1L);

        assertNull(result);

        verify(actionRepository, times(1)).findById(1L);
    }


    @Test
    void getAllActions_ShouldReturnEmptySet_WhenNoNonDeletedActions() {
        Set<Action> actions = new HashSet<>();
        action2.setDeleted(true);
        actions.add(action2);

        when(actionRepository.findAll()).thenReturn(new ArrayList<>(actions));

        Set<ActionDto> result = actionService.getAllActions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(actionRepository, times(1)).findAll();
    }


    @Test
    void getAllActions_ShouldReturnActionDtos_WhenOnlyNonDeletedActionsPresent() {
        action2.setDeleted(false);
        Set<Action> actions = new HashSet<>();
        actions.add(action);
        actions.add(action2);

        when(actionRepository.findAll()).thenReturn(new ArrayList<>(actions));
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);
        when(actionMapper.actionToActionDto(action2)).thenReturn(actionDto2);

        Set<ActionDto> result = actionService.getAllActions();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(actionRepository, times(1)).findAll();
        verify(actionMapper, times(1)).actionToActionDto(action);
        verify(actionMapper, times(1)).actionToActionDto(action2);
    }



    /*
    @Test
    void updateAction_ShouldReturnUpdatedActionDto_WhenActionIsFoundAndNotDeleted() {
        ActionDto updatedActionDto = new ActionDto();
        updatedActionDto.setId(1L);
        updatedActionDto.setActionCreatedBy(20L);
        updatedActionDto.setComments(Set.of("Updated Comment"));
        updatedActionDto.setValidationStatus(ValidationStatus.REFUSER);

        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);
        when(actionMapper.actionDtoToAction(any(ActionDto.class))).thenReturn(action);
        when(actionRepository.save(action)).thenReturn(action);

        ActionDto result = actionService.updateAction(1L, updatedActionDto);

        assertNotNull(result);
        assertEquals(updatedActionDto.getId(), result.getId());
        assertEquals(updatedActionDto.getActionCreatedBy(), result.getActionCreatedBy());
        assertEquals(updatedActionDto.getComments(), result.getComments());
        assertEquals(updatedActionDto.getValidationStatus(), result.getValidationStatus());

        verify(actionRepository, times(1)).findById(1L);
        verify(actionMapper, times(1)).actionToActionDto(action);
        verify(actionRepository, times(1)).save(action);
        verify(actionMapper, times(1)).actionDtoToAction(any(ActionDto.class));
    }

     */

    /*
    @Test
    void updateAction_ShouldThrowException_WhenActionIsNotFound() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ActionDto updatedActionDto = new ActionDto();
        updatedActionDto.setId(1L);

        assertThrows(ActionNotFoundException.class, () -> {
            actionService.updateAction(1L, updatedActionDto);
        });

        verify(actionRepository, times(1)).findById(1L);
    }

     */


    /*
    @Test
    void updateAction_ShouldReturnNull_WhenActionIsDeleted() {
        ActionDto updatedActionDto = new ActionDto();
        updatedActionDto.setId(1L);
        updatedActionDto.setActionCreatedBy(20L);
        updatedActionDto.setComments(Set.of("Updated Comment"));
        updatedActionDto.setValidationStatus(ValidationStatus.REFUSER);

        action.setDeleted(true);

        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));

        ActionDto result = actionService.updateAction(1L, updatedActionDto);

        assertNull(result);

        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(0)).save(action);
    }

     */


    /*
    @Test
    void updateAction_ShouldReturnUpdatedActionDto_WhenValidationStatusChanged() {
        ActionDto updatedActionDto = new ActionDto();
        updatedActionDto.setId(1L);
        updatedActionDto.setActionCreatedBy(20L);
        updatedActionDto.setComments(Set.of("Updated Comment"));
        updatedActionDto.setValidationStatus(ValidationStatus.ACCEPTER);

        // Mocking action repository and mapper
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));
        when(actionMapper.actionToActionDto(action)).thenReturn(actionDto);
        when(actionMapper.actionDtoToAction(any(ActionDto.class))).thenReturn(action);
        when(actionRepository.save(action)).thenReturn(action);

        ActionDto result = actionService.updateAction(1L, updatedActionDto);

        assertNotNull(result);
        assertEquals(ValidationStatus.ACCEPTER, result.getValidationStatus());

        verify(actionRepository, times(1)).findById(1L);
        verify(actionMapper, times(1)).actionToActionDto(action);
        verify(actionRepository, times(1)).save(action);
        verify(actionMapper, times(1)).actionDtoToAction(any(ActionDto.class));
    }

     */



    @Test
    void deleteAction_ShouldMarkActionAsDeleted_WhenActionIsFound() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.of(action));

        actionService.deleteAction(1L);

        assertTrue(action.isDeleted());

        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).save(action);
    }

    @Test
    void deleteAction_ShouldThrowException_WhenActionIsNotFound() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> {
            actionService.deleteAction(1L);
        });

        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(0)).save(any(Action.class));
    }

    @Test
    void deleteAction_ShouldNotSave_WhenActionIsNotFound() {
        when(actionRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> {
            actionService.deleteAction(1L);
        });

        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(0)).save(any(Action.class));
    }


}
