package ma.inwi.msproject.service;

import ma.inwi.msproject.client.ProjectRestClient;
import ma.inwi.msproject.client.UserClient;
import ma.inwi.msproject.configuration.RabbitMQConfig;
import ma.inwi.msproject.dto.*;
import ma.inwi.msproject.dto.notifications.NewImpactAddedMailDto;
import ma.inwi.msproject.dto.notifications.NewProjectCreationMailDto;
import ma.inwi.msproject.dto.notifications.NewResponseToImpactMailDto;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.exceptions.*;
import ma.inwi.msproject.mappers.ActionMapper;
import ma.inwi.msproject.repositories.ActionRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActionService {

    private final ActionRepository actionRepository;
    private final RequiredActionRepository requiredActionRepository;
    private final ActionMapper actionMapper;
    private final ProjectRestClient projectRestClient;
    private final UserClient client;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ActionService(ActionRepository actionRepository, ActionMapper actionMapper, ProjectRestClient projectRestClient, RequiredActionRepository requiredActionRepository, UserClient client, RabbitTemplate rabbitTemplate) {
        this.actionRepository = actionRepository;
        this.actionMapper = actionMapper;
        this.projectRestClient = projectRestClient;
        this.requiredActionRepository = requiredActionRepository;
        this.client = client;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ActionDto addAction(ActionDto actionDto){
        Optional<Action> actionOptional = Optional.ofNullable(actionRepository.findActionByImpact(actionDto.getActionLabel()));

        if (actionOptional.isPresent()){
            throw new ActionAlreadyExistingException("The action with ID " + actionOptional.get().getId() + " already exists !");
        }

        Action action = actionMapper.actionDtoToAction(actionDto);
        actionRepository.save(action);
        notifyUsersAboutNewImpact(actionDto);
        return actionMapper.actionToActionDto(action);
    }

    public ActionDto getActionById(Long id){
        Action action = actionRepository.findById(id).orElseThrow(
                () -> new ActionNotFoundException("Action with ID " + id + " was not found ! "));

        if (action.isDeleted()){
            return null;
        }

        return actionMapper.actionToActionDto(action);
    }

    public Set<ActionDto> getAllActions(){
        List<Action> actions = actionRepository.findAll();
        if (actions.isEmpty()){
            throw new ActionNotFoundException("No action is found !");
        }

        return actions.stream()
                .filter(action -> !action.isDeleted())
                .map(actionMapper::actionToActionDto)
                .collect(Collectors.toSet());
    }

    public ActionDto updateImpact(Long id, ActionDto updatedAction){
        Action action = actionRepository.findById(id).orElseThrow(
                () -> new ActionNotFoundException("Action with ID " + id + " was not found ! "));

        if (action.isDeleted()){
            return null;
        }

        ActionDto existingAction = actionMapper.actionToActionDto(action);
        //existingAction.setComments(updatedAction.getComments());
        existingAction.setActionDocument(updatedAction.getActionDocument());

        actionRepository.save(actionMapper.actionDtoToAction(existingAction));

        notifyUsersAboutImpactModification(existingAction);

        return existingAction;
    }


    public ActionDto validateImpact(Long id, ActionDto validatedImpact){
        Action action = actionRepository.findById(id).orElseThrow(
                () -> new ActionNotFoundException("Action with ID " + id + " was not found ! "));

        if (action.isDeleted()){
            return null;
        }

        ActionDto existingAction = actionMapper.actionToActionDto(action);

        existingAction.setResponseToActionLabel(validatedImpact.getResponseToActionLabel());
        existingAction.setValidationStatus(validatedImpact.getValidationStatus());
        existingAction.setJustificationStatus(validatedImpact.getJustificationStatus());
        existingAction.setRequiredActionId(validatedImpact.getRequiredActionId());
        existingAction.setValidatedBy(validatedImpact.getValidatedBy());
        existingAction.setResponseDocument(validatedImpact.getResponseDocument());
        existingAction.setResponseEmailSender(validatedImpact.getResponseEmailSender());

        actionRepository.save(actionMapper.actionDtoToAction(existingAction));
        notifyUserOnImpactResponse(existingAction);
        return existingAction;
    }



    public void deleteAction(Long id){
        Action action = actionRepository.findById(id).orElseThrow(
                () -> new ActionNotFoundException("Action with ID " + id + " was not found ! "));
        action.setDeleted(true);
        actionRepository.save(action);
    }



    public DocumentDto uploadFile(MultipartFile file){
        return projectRestClient.uploadFile(file).getBody();
    }

    public DocumentDto updateDocument(Long id, DocumentDto documentDto){
        return projectRestClient.updateFile(id, documentDto).getBody();
    }

    public String getFileSize(String fileName){
        return projectRestClient.getFileSize(fileName).getBody();
    }

    public String getFileLastModified(String fileName){
        return projectRestClient.getFileLastModified(fileName).getBody();
    }


    public DocumentResponseFrontDto getDocumentAction(Long id) {
        try {
            Action action = actionRepository.findById(id).orElseThrow(
                    () -> new ActionNotFoundException("Action with ID " + id + " was not found!")
            );

            String document = action.getActionDocument();
            if (document == null || document.isEmpty()) {
                throw new DocumentNotFoundException("No document associated with the action.");
            }

            String actionLabel = action.getActionLabel();
            String typeDocument = document.substring(document.lastIndexOf(".") + 1);
            String authorName = "Yassine";

            String dateUpload;
            String fileActionSize;
            try {
                dateUpload = getFileLastModified(document);
                fileActionSize = getFileSize(document);
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving file metadata: " + e.getMessage(), e);
            }

            RequiredAction requiredAction = action.getRequiredAction();
            DepartementGateProject departementGateProject = requiredAction.getDepartementGateProject();
            Departement departement = departementGateProject.getDepartement();
            GateProject gateProject = departementGateProject.getGateProject();
            Gate gate = gateProject.getTrackingGate().getGate();

            return DocumentResponseFrontDto.builder()
                    .actionLabel(actionLabel)
                    .typeDocument(typeDocument)
                    .department(departement.getDepartement())
                    .authorName(authorName)
                    .gateLabel(gate.getGateType())
                    .dateUpload(dateUpload)
                    .size(fileActionSize)
                    .build();

        } catch (ActionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get document action: " + e.getMessage(), e);
        }
    }

    public DocumentResponseFrontDto getDocumentActionResponse(Long id) {
        try {
            Action action = actionRepository.findById(id).orElseThrow(
                    () -> new ActionNotFoundException("Action with ID " + id + " was not found!")
            );

            String responseDocument = action.getResponseDocument();
            if (responseDocument == null || responseDocument.isEmpty()) {
                throw new IllegalStateException("No response document associated with the action.");
            }

            String responseToActionLabel = action.getResponseToActionLabel();
            String typeResponseDocument = responseDocument.substring(responseDocument.lastIndexOf(".") + 1);
            String authorName = "Yassine";

            String dateUpload;
            String fileActionResponseSize;
            try {
                dateUpload = getFileLastModified(responseDocument);
                fileActionResponseSize = getFileSize(responseDocument);
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving response file metadata: " + e.getMessage(), e);
            }

            RequiredAction requiredAction = action.getRequiredAction();
            DepartementGateProject departementGateProject = requiredAction.getDepartementGateProject();
            Departement departement = departementGateProject.getDepartement();
            GateProject gateProject = departementGateProject.getGateProject();
            Gate gate = gateProject.getTrackingGate().getGate();

            return DocumentResponseFrontDto.builder()
                    .actionLabel(responseToActionLabel)
                    .typeDocument(typeResponseDocument)
                    .department(departement.getDepartement())
                    .authorName(authorName)
                    .gateLabel(gate.getGateType())
                    .dateUpload(dateUpload)
                    .size(fileActionResponseSize)
                    .build();

        } catch (ActionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get document action response: " + e.getMessage(), e);
        }
    }







    //Notifications
    public void notifyUserOnImpactResponse(ActionDto actionDto){
        Project project = actionRepository.findProjectByRequiredAction(actionDto.getRequiredActionId());
        UserDetails userDetails = UserDetails.builder()
                .username(actionDto.getActionCreatedBy())
                .projectName(project.getTitle())
                .email(actionDto.getImpactSenderEmail())
                .build();

        NewResponseToImpactMailDto newResponseToImpactMailDto = NewResponseToImpactMailDto.builder()
                .userDetails(userDetails)
                .responseToActionLabel(actionDto.getResponseToActionLabel())
                .validationStatus(actionDto.getValidationStatus())
                .justificationStatus(actionDto.getJustificationStatus())
                .validatedBy(actionDto.getValidatedBy())
                .responseEmailSender(actionDto.getResponseEmailSender())
                .responseDocument(actionDto.getResponseDocument())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RESPONSE_ADDED_ROUTING_KEY, newResponseToImpactMailDto);
    }


    public void notifyUsersAboutNewImpact(ActionDto actionDto){
        Project project = actionRepository.findProjectByRequiredAction(actionDto.getRequiredActionId());
        List<UserDto> dtoList = client.getUsersByDepartments(project.getDepartments());

        List<NewImpactAddedMailDto> usersList = dtoList.stream()
                .map(dto -> mapToNewImpactAddedMailDto(dto, actionDto))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.IMPACT_ADDED_ROUTING_KEY, usersList);
    }


    public void notifyUsersAboutImpactModification(ActionDto actionDto){
        Project project = actionRepository.findProjectByRequiredAction(actionDto.getRequiredActionId());
        List<UserDto> dtoList = client.getUsersOfAllDepartments(project.getDepartments());

        List<NewImpactAddedMailDto> usersList = dtoList.stream()
                .map(dto -> mapToNewImpactAddedMailDto(dto, actionDto))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.IMPACT_MODIFICATION_ROUTING_KEY, usersList);
    }



    private UserDetails mapToUserDetails(UserDto dto, String projectName) {
        UserDetails details = new UserDetails();
        details.setProjectName(projectName);
        details.setLastName(dto.getLastName());
        details.setFirstName(dto.getFirstName());
        details.setDepartment(dto.getDepartment());
        details.setEmail(dto.getEmail());
        return details;
    }

    private NewImpactAddedMailDto mapToNewImpactAddedMailDto(UserDto dto, ActionDto actionDto){
        Project project = actionRepository.findProjectByRequiredAction(actionDto.getRequiredActionId());
        RequiredAction requiredAction = requiredActionRepository.findById(actionDto.getRequiredActionId()).orElseThrow(
                () -> new RequiredActionNotFoundException("Required action not found !"));

        NewImpactAddedMailDto mailDto = new NewImpactAddedMailDto();
        UserDetails userDetails = mapToUserDetails(dto, project.getTitle());
        mailDto.setUserDetails(userDetails);

        mailDto.setActionLabel(actionDto.getActionLabel());
        mailDto.setComments(actionDto.getComments());
        mailDto.setActionCreatedBy(actionDto.getActionCreatedBy());
        mailDto.setImpactSenderEmail(actionDto.getImpactSenderEmail());
        mailDto.setActionDocument(actionDto.getActionDocument());
        mailDto.setRequiredAction(requiredAction.getRequiredAction());

        return mailDto;
    }



    public Set<ActionDto> getImpactAndReturnsByInterlocutor(String impactUsername){
        List<Action> actions = actionRepository.findImpactsByInterlocutor(impactUsername);
        return actions.stream()
                .map(actionMapper::actionToActionDto)
                .collect(Collectors.toSet());
    }

    public Set<ActionDto> getActionsByRequiredAction(Long requiredActionId){
        List<Action> actions = actionRepository.findActionsByRequiredActionId(requiredActionId);
        return actions.stream()
                .map(actionMapper::actionToActionDto)
                .collect(Collectors.toSet());
    }




}

