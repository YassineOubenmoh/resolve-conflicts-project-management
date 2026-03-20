package ma.inwi.msproject.service;

import ma.inwi.msproject.client.UserClient;
import ma.inwi.msproject.configuration.RabbitMQConfig;
import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.dto.GateProjectResponseDto;
import ma.inwi.msproject.dto.UserDetails;
import ma.inwi.msproject.dto.UserDto;
import ma.inwi.msproject.dto.frontdtos.GateProjectFront;
import ma.inwi.msproject.dto.notifications.GateProgressSuspendedMailDto;
import ma.inwi.msproject.dto.notifications.NewProjectCreationMailDto;
import ma.inwi.msproject.dto.notifications.SuspensionGateMailDto;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.GateProjectNotFoundException;
import ma.inwi.msproject.exceptions.ProjectNotFoundException;
import ma.inwi.msproject.exceptions.UnauthorizedUpdateFinalGateException;
import ma.inwi.msproject.mappers.GateProjectMapper;
import ma.inwi.msproject.repositories.GateProjectRepository;
import ma.inwi.msproject.repositories.GateRepository;
import ma.inwi.msproject.repositories.ProjectRepository;
import org.glassfish.jaxb.core.v2.TODO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GateProjectService {

    Logger logger = LoggerFactory.getLogger(GateProjectService.class);

    private final GateProjectRepository gateProjectRepository;
    private final GateProjectMapper gateProjectMapper;
    private final ProjectRepository projectRepository;
    private final GateRepository gateRepository;
    private final UserClient client;
    private final RabbitTemplate rabbitTemplate;
    private final ProjectService projectService;

    @Autowired
    public GateProjectService(GateProjectRepository gateProjectRepository,
                              GateProjectMapper gateProjectMapper,
                              ProjectRepository projectRepository,
                              GateRepository gateRepository,
                              UserClient client,
                              RabbitTemplate rabbitTemplate,
                              ProjectService projectService) {
        this.gateProjectRepository = gateProjectRepository;
        this.gateProjectMapper = gateProjectMapper;
        this.projectRepository = projectRepository;
        this.gateRepository = gateRepository;
        this.client = client;
        this.rabbitTemplate = rabbitTemplate;
        this.projectService = projectService;
    }


    public GateProjectDto addGateProject(GateProjectDto gateProjectRequest){
        GateProject gateProject = gateProjectMapper.gateProjectDtoToGateProject(gateProjectRequest);
        gateProjectRepository.save(gateProject);
        return gateProjectMapper.gateProjectToGateProjectDto(gateProject);
    }

    public GateProjectDto getGateProjectById(Long id){
        GateProject gateProject = gateProjectRepository.findById(id).orElseThrow(
                () -> new GateProjectNotFoundException("Gate Project with ID " + id + " was not found ! "));

        if (gateProject.isDeleted()){
            return null;
        }

        return gateProjectMapper.gateProjectToGateProjectDto(gateProject);
    }

    public Set<GateProjectDto> getAllGateProjects(){
        return gateProjectRepository.findAll().stream()
                .filter(gateProject -> !gateProject.isDeleted())
                .map(gateProjectMapper::gateProjectToGateProjectDto)
                .collect(Collectors.toSet());
    }

    public GateProjectDto updateGateProject(Long id, GateProjectDto updatedGateProject){

        GateProject existingGateProject = gateProjectRepository.findById(id).orElseThrow(
                () -> new GateProjectNotFoundException("Gate Project with " + id + " was not found ! ")
        );

        if (existingGateProject.isDeleted()){
            return null;
        }

        existingGateProject.setInProgress(updatedGateProject.isInProgress());
        existingGateProject.setDecisionType(updatedGateProject.getDecisionType());
        existingGateProject.setInformation(updatedGateProject.getInformation());
        existingGateProject.setActions(updatedGateProject.getActions());
        existingGateProject.setDecisions(updatedGateProject.getDecisions());

        gateProjectRepository.save(existingGateProject);
        return gateProjectMapper.gateProjectToGateProjectDto(existingGateProject);
    }

    public Set<GateProjectResponseDto> getGatesResponseProjectByProjectId(Long projectId){
        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);

        return gateProjects.stream()
                .filter(gateProject -> !gateProject.isDeleted())
                .map(gateProjectMapper::gateProjectToGateProjectResponseDto)
                .collect(Collectors.toSet());
    }

    public Set<GateProjectDto> getGatesProjectByProjectId(Long projectId){

        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);
        if (gateProjects.isEmpty()){
            throw new ProjectNotFoundException("Project with ID " + projectId + " was not found !");
        }

        return gateProjects.stream()
                .filter(gateProject -> !gateProject.isDeleted())
                .map(gateProjectMapper::gateProjectToGateProjectDto)
                .collect(Collectors.toSet());
    }

    public void deleteGateProject(Long id){
        GateProject gateProject = gateProjectRepository.findById(id).orElseThrow(
                () -> new GateProjectNotFoundException("Project with " + id + " was not found ! "));
        gateProject.setDeleted(true);
        gateProjectRepository.save(gateProject);
    }



    public GateProjectDto suspendCurrentGateProject(Long projectId){
        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);
        if (gateProjects.isEmpty()){
            throw new GateProjectNotFoundException("No concrete gate was found !");
        }
        for (GateProject gateProject : gateProjects){
            if (gateProject.isCurrentGate()){
                gateProject.setInProgress(false);
                return gateProjectMapper.gateProjectToGateProjectDto(gateProject);
            }
        }
        return null;
    }

    public void notifyUsersOnGateSuspension(GateProject gateProject, String senderMail){
        List<UserDto> dtoList = client.getUsersByDepartments(gateProject.getProject().getDepartments());

        List<GateProgressSuspendedMailDto> usersList = dtoList.stream()
                .map(dto -> mapToGateProgressSuspendedMailDto(dto, gateProject, senderMail))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, usersList);
    }

    public GateProgressSuspendedMailDto mapToGateProgressSuspendedMailDto(UserDto userDto, GateProject gateProject, String senderMail){
        UserDetails userDetails = mapToUserDetails(userDto, gateProject.getProject().getTitle());
        GateProgressSuspendedMailDto gateProgressSuspendedMailDto = GateProgressSuspendedMailDto.builder()
                .userDetails(userDetails)
                .gateType(gateProject.getTrackingGate().getGate().getGateType())
                .senderEmail(senderMail)
                .build();
        return gateProgressSuspendedMailDto;
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



    public GateProjectDto changeGateProgress(Long gateProjectId, String emailOwner) {
        GateProject gateProject = gateProjectRepository.findById(gateProjectId)
                .orElseThrow(() -> new GateProjectNotFoundException("Concrete gate with ID : " + gateProjectId + " was not found !"));

        gateProject.setInProgress(!gateProject.isInProgress());
        gateProjectRepository.save(gateProject);
        notifyUsersOnGateProgressStatus(gateProject.getProject(), gateProject, emailOwner);

        return gateProjectMapper.gateProjectToGateProjectDto(gateProject);
    }



    private void notifyUsersOnGateProgressStatus(Project project, GateProject gateProject, String emailOwner) {
        List<UserDto> dtoList = client.getUsersByDepartments(project.getDepartments());

        List<SuspensionGateMailDto> usersList = dtoList.stream()
                .map(dto -> mapToSuspensionGateMailDto(dto, project.getTitle(), gateProject, emailOwner))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.GATE_SUSPENSION_ROUTING_KEY, usersList);
    }


    private SuspensionGateMailDto mapToSuspensionGateMailDto(UserDto dto, String projectName, GateProject gateProject, String emailOwner) {
        GateType gateType = gateProject.getTrackingGate().getGate().getGateType();
        SuspensionGateMailDto suspensionGateMailDto = new SuspensionGateMailDto();
        UserDetails details = mapToUserDetails(dto, projectName);
        suspensionGateMailDto.setUserDetails(details);
        suspensionGateMailDto.setInProgress(gateProject.isInProgress());
        suspensionGateMailDto.setGate(gateType);
        suspensionGateMailDto.setEmailOwner(emailOwner);

        return suspensionGateMailDto;
    }

    public Long getCurrentGateIdByProjectId(Long projectId) {
        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);

        if (gateProjects.isEmpty()) {
            throw new ProjectNotFoundException("Project with ID " + projectId + " was not found !");
        }

        GateProject currentGateProject = gateProjects.stream()
                .filter(gateProject -> !gateProject.isDeleted() && gateProject.isCurrentGate())
                .findFirst()
                .orElseThrow(() -> new GateProjectNotFoundException("No current gate found for project with ID " + projectId));

        return currentGateProject.getId();
    }



    public boolean hasFinishedProject(Long projectId) {
        if (projectId == null) {
            return false;
        }

        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);
        if (gateProjects == null || gateProjects.isEmpty()) {
            return false;
        }

        for (GateProject gateProject : gateProjects) {
            TrackingGate trackingGate = gateProject.getTrackingGate();
            if (trackingGate != null && trackingGate.getGateAfter() == null && gateProject.isProjectCompleted()) {
                return true;
            }
        }

        return false;
    }

    public Set<Project> getCompletedProjects(String department) {
        if (department == null || department.isBlank()) {
            return Collections.emptySet();
        }

        List<Project> projects = projectRepository.findProjectsByDepartment(department);
        if (projects == null || projects.isEmpty()) {
            return Collections.emptySet();
        }

        return projects.stream()
                .filter(project -> project != null && hasFinishedProject(project.getId()))
                .collect(Collectors.toSet());
    }


}
