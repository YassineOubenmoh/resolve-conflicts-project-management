package ma.inwi.msproject.service;

import lombok.Builder;
import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.dto.RequiredActionGlobalResponse;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.*;
import ma.inwi.msproject.mappers.RequiredActionMapper;
import ma.inwi.msproject.repositories.DepartementGateProjectRepository;
import ma.inwi.msproject.repositories.DepartementRepository;
import ma.inwi.msproject.repositories.ProjectRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Builder
public class RequiredActionService {

    private final RequiredActionRepository requiredActionRepository;
    private final RequiredActionMapper requiredActionMapper;
    private final DepartementGateProjectRepository departementGateProjectRepository;
    private final ProjectRepository projectRepository;
    private final DepartementRepository departementRepository;

    @Autowired
    public RequiredActionService(RequiredActionRepository requiredActionRepository, RequiredActionMapper requiredActionMapper, DepartementGateProjectRepository departementGateProjectRepository, ProjectRepository projectRepository, DepartementRepository departementRepository) {
        this.requiredActionRepository = requiredActionRepository;
        this.requiredActionMapper = requiredActionMapper;
        this.departementGateProjectRepository = departementGateProjectRepository;
        this.projectRepository = projectRepository;
        this.departementRepository = departementRepository;
    }

    public RequiredActionDto addActionRequise(RequiredActionDto requiredActionDto) {
        Optional<RequiredAction> requiredActionOptional = requiredActionRepository.findRequiredActionAndDepartementGateProject(
                requiredActionDto.getRequiredAction(), requiredActionDto.getDepartementGateProjectId());

        if (requiredActionOptional.isPresent()){
            throw new RequiredActionAlreadyExistingException("The link of required action with concrete gate with ID " +
                    requiredActionOptional.get().getDepartementGateProject() + " already exists !");
        }

        RequiredAction requiredAction = requiredActionMapper.requiredActionDtoToRequiredAction(requiredActionDto);
        requiredActionRepository.save(requiredAction);
        return requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction);
    }


    public RequiredActionDto getActionRequiseById(Long id) {
        RequiredAction requiredAction = requiredActionRepository.findById(id).orElseThrow(
                () -> new RequiredActionNotFoundException("Action Requise with " + id + " was not found !"));

        if (requiredAction.isDeleted()){
            return null;
        }

        return requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction);
    }

    public Set<RequiredActionDto> getAllActionRequises() {
        List<RequiredAction> requiredActions = requiredActionRepository.findAll();
        if (requiredActions.isEmpty()){
            throw new RequiredActionNotFoundException("No required action is found !");
        }

        return requiredActions.stream()
                .filter(requiredAction -> !requiredAction.isDeleted())
                .map(requiredActionMapper::requiredActiontoRequiredActionDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public RequiredActionDto updateActionRequise(Long id, RequiredActionDto updatedRequiredActionDto) {
        RequiredAction existingRequiredAction = requiredActionRepository.findById(id).orElseThrow(
                () -> new RequiredActionNotFoundException("Action Requise with " + id + " was not found !"));

        DepartementGateProject departementGateProject = departementGateProjectRepository.findById(updatedRequiredActionDto.getDepartementGateProjectId()).orElseThrow(
                () -> new DepartementGateProjectNotFoundException("Departement Gate with " + updatedRequiredActionDto.getDepartementGateProjectId() + " was not found !"));

        if (existingRequiredAction.isDeleted() || departementGateProject.isDeleted()){
            return null;
        }

        existingRequiredAction.setRequiredAction(updatedRequiredActionDto.getRequiredAction());
        existingRequiredAction.setDepartementGateProject(departementGateProject);

        requiredActionRepository.save(existingRequiredAction);

        return requiredActionMapper.requiredActiontoRequiredActionDto(existingRequiredAction);
    }


    public void deleteActionRequise(Long id) {
        RequiredAction requiredAction = requiredActionRepository.findById(id).orElseThrow(
                () -> new RequiredActionNotFoundException("Action requise with " + id + " was not found !"));
        requiredAction.setDeleted(true);
        requiredActionRepository.save(requiredAction);
    }

    @Transactional
    public RequiredActionGlobalResponse getRequiredActionGlobalInfoById(Long requiredActionId){
        RequiredAction requiredAction = requiredActionRepository.findById(requiredActionId).orElseThrow(
                () -> new RequiredActionNotFoundException("The affectation wasn't not found !"));

        if (requiredAction.isDeleted()){
            return null;
        }

        DepartementGateProject departementGateProject = requiredAction.getDepartementGateProject();
        Departement departement = departementGateProject.getDepartement();
        GateType gateType = departementGateProject.getGateProject().getTrackingGate().getGate().getGateType();
        String projectTitle = departementGateProject.getGateProject().getProject().getTitle();

        return RequiredActionGlobalResponse.builder()
                .requiredAction(requiredAction.getRequiredAction())
                .departement(departement.getDepartement())
                .gateType(gateType)
                .projectTitle(projectTitle)
                .build();

    }


    public Set<RequiredActionDto> getRequiredActionsByProjectId(Long projectId){
        List<RequiredAction> requiredActions = requiredActionRepository.findRequiredActionByProjectId(projectId);
        if (requiredActions.isEmpty()){
            throw new RequiredActionNotFoundException("No required action exist !");
        }

        return requiredActions.stream()
                .map(requiredActionMapper::requiredActiontoRequiredActionDto)
                .collect(Collectors.toSet());
    }


    public Set<RequiredActionDto> getRequiredActionsByProjectIdAndGate(Long projectId, GateType gateType){
        List<RequiredAction> requiredActions = requiredActionRepository.findRequiredActionByProjectId(projectId);
        if (requiredActions.isEmpty()){
            throw new RequiredActionNotFoundException("No required action exist !");
        }

        return requiredActions.stream()
                .filter(requiredAction -> requiredAction.getDepartementGateProject().getGateProject().getTrackingGate().getGate().getGateType() == gateType)
                .map(requiredActionMapper::requiredActiontoRequiredActionDto)
                .collect(Collectors.toSet());
    }

    //No redundant elements on required actions
    public RequiredActionDto getRequiredActionIdFromLabel(String requiredActionLabel, Long projectId){
        List<RequiredAction> requiredActions = requiredActionRepository.findRequiredActionByProjectId(projectId);
        for (RequiredAction requiredAction : requiredActions){
            if (Objects.equals(requiredAction.getRequiredAction(), requiredActionLabel)){
                return requiredActionMapper.requiredActiontoRequiredActionDto(requiredAction);
            }
        }
        return null;
    }









}
