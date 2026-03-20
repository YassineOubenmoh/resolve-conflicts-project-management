package ma.inwi.msproject.service;

import ma.inwi.msproject.client.UserClient;
import ma.inwi.msproject.configuration.RabbitMQConfig;
import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.dto.GateProjectResponseDto;
import ma.inwi.msproject.dto.UserDetails;
import ma.inwi.msproject.dto.UserDto;
import ma.inwi.msproject.dto.frontdtos.GateProjectFront;
import ma.inwi.msproject.dto.notifications.DepartmentGateRequiredAction;
import ma.inwi.msproject.dto.notifications.DepartmentsGatesAffectationEmailDto;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.exceptions.*;
import ma.inwi.msproject.mappers.DepartementGateProjectMapper;
import ma.inwi.msproject.mappers.GateProjectMapper;
import ma.inwi.msproject.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartementGateProjectService {

    private static final Logger logger = LoggerFactory.getLogger(DepartementGateProjectService.class);

    private final DepartementGateProjectRepository departementGateProjectRepository;
    private final DepartementGateProjectMapper departementGateProjectMapper;
    private final DepartementRepository departementRepository;
    private final GateProjectRepository gateProjectRepository;
    private final ConfigRequiredActionRepository configRequiredActionRepository;
    private final RequiredActionRepository requiredActionRepository;
    private final GateProjectMapper gateProjectMapper;
    private final ProjectRepository projectRepository;


    private final UserClient client;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public DepartementGateProjectService(DepartementGateProjectRepository departementGateProjectRepository,
                                         DepartementGateProjectMapper departementGateProjectMapper,
                                         DepartementRepository departementRepository,
                                         GateProjectRepository gateProjectRepository,
                                         ConfigRequiredActionRepository configRequiredActionRepository,
                                         RequiredActionRepository requiredActionRepository,
                                         UserClient client,
                                         RabbitTemplate rabbitTemplate,
                                         GateProjectMapper gateProjectMapper, ProjectRepository projectRepository) {
        this.departementGateProjectRepository = departementGateProjectRepository;
        this.departementGateProjectMapper = departementGateProjectMapper;
        this.departementRepository = departementRepository;
        this.gateProjectRepository = gateProjectRepository;
        this.configRequiredActionRepository = configRequiredActionRepository;
        this.requiredActionRepository = requiredActionRepository;
        this.client = client;
        this.rabbitTemplate = rabbitTemplate;
        this.gateProjectMapper = gateProjectMapper;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public DepartementGateProjectDto affectGateProjectToDepartement(Long gateProjectId, Long departementId, String emailSender) {

        GateProject gateProject = gateProjectRepository.findById(gateProjectId).orElseThrow(
                () -> new GateProjectNotFoundException("The gate of project with the id " + gateProjectId + " was not found !"));
        Departement departement = departementRepository.findById(departementId).orElseThrow(
                () -> new DepartementNotFoundException("The departement with id " + departementId + " was not found !"));

        if (gateProject.isDeleted() || departement.isDeleted()) {
            return null;
        }

        DepartementGateProject departementGateProject = DepartementGateProject.builder()
                .gateProject(gateProject)
                .departement(departement)
                .build();

        departementGateProjectRepository.save(departementGateProject);

        DepartementGateProjectDto departementGateProjectDto = addRequiredActions(departementGateProject);


        notifySpocsOnGatesAffectationToDepartment(departementGateProjectDto, departement.getDepartement(), emailSender);

        logger.info("Affectation result - DepartementGateProjectDto: {}", departementGateProjectDto);
        return departementGateProjectDto;
    }



    @Transactional
    public List<DepartementGateProjectDto> affectGateProjectToListDepartements(Long gateProjectId, List<String> departementLabels, String emailSender) {
        // Fetch the GateProject
        GateProject gateProject = gateProjectRepository.findById(gateProjectId).orElseThrow(
                () -> new GateProjectNotFoundException("The gate project with id " + gateProjectId + " was not found!")
        );

        // If the GateProject is deleted, return null
        if (gateProject.isDeleted()) {
            return null;
        }

        List<Departement> departements = new ArrayList<>();

        // Validate all departements
        for (String departementLabel : departementLabels) {
            Optional<Departement> optionalDepartement = departementRepository.findDepartementByLabel(departementLabel);

            // If departement not found, throw exception
            Departement departement = optionalDepartement.orElseThrow(
                    () -> new DepartementNotFoundException("Departement named: " + departementLabel + " was not found!")
            );

            if (departement.isDeleted()) {
                return null;
            }

            departements.add(departement);
        }

        List<DepartementGateProjectDto> departementGateProjectDtos = new ArrayList<>();

        for (Departement departement : departements) {
            DepartementGateProjectDto dto = affectGateProjectToDepartement(gateProjectId, departement.getId(), emailSender);
            if (dto != null) {
                departementGateProjectDtos.add(dto);
            }
        }

        return departementGateProjectDtos;
    }








    public DepartementGateProjectDto modifyAffectedGateToDepartement(Long id, Long gateProjectId){
        DepartementGateProject departementGateProject = departementGateProjectRepository.findById(id).orElseThrow(
                () -> new DepartementGateProjectNotFoundException("The Affectation with id " + id + " was not found !"));

        GateProject gateProject = gateProjectRepository.findById(gateProjectId).orElseThrow(
                () -> new GateProjectNotFoundException("The gate of project with the id " + gateProjectId + " was not found !"));

        if (departementGateProject.isDeleted() || gateProject.isDeleted()){
            return null;
        }

        departementGateProject.setGateProject(gateProject);
        return departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject);
    }

    public DepartementGateProjectDto getDepartementGateProjectAffectationById(Long id) {
        DepartementGateProject departementGateProject = departementGateProjectRepository.findById(id).orElseThrow(
                () -> new DepartementGateProjectNotFoundException("Departement GateProject with " + id + " was not found !"));

        if (departementGateProject.isDeleted()){
            return null;
        }

        return departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(departementGateProject);
    }

    public Set<DepartementGateProjectDto> getAllProjectGatesAffectationsToDepartement() {
        List<DepartementGateProject> departementGateProjects = departementGateProjectRepository.findAll();
        if (departementGateProjects.isEmpty()){
            throw new DepartementGateProjectNotFoundException("No affectation was found !");
        }

        return departementGateProjects.stream()
                .filter(departementGateProject -> !departementGateProject.isDeleted())
                .map(departementGateProjectMapper::departementGateProjectToDepartementGateProjectDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public DepartementGateProjectDto updateGateProjectAffectationToDepartement(Long id, DepartementGateProjectDto updatedDepartementGateProjectDto) {
        DepartementGateProject existingDepartementGateProject = departementGateProjectRepository.findById(updatedDepartementGateProjectDto.getId()).orElseThrow(
                () -> new DepartementGateProjectNotFoundException("Departement gate with " + id + " was not found !"));

        Departement departement = departementRepository.findById(updatedDepartementGateProjectDto.getDepartementId()).orElseThrow(
                () -> new DepartementNotFoundException("Departement with " + id + " was not found !"));

        GateProject gateProject = gateProjectRepository.findById(updatedDepartementGateProjectDto.getGateProjectId()).orElseThrow(
                () -> new GateNotFoundException("Gate Project with " + id + " was not found !"));

        if (existingDepartementGateProject.isDeleted() || departement.isDeleted() || gateProject.isDeleted()){
            return null;
        }

        existingDepartementGateProject.setDepartement(departement);
        existingDepartementGateProject.setGateProject(gateProject);

        departementGateProjectRepository.save(existingDepartementGateProject);

        return departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(existingDepartementGateProject);
    }

    public void deleteGateProjectAffectationToDepartement(Long id) {
        DepartementGateProject departementGateProject = departementGateProjectRepository.findById(id).orElseThrow(
                () -> new DepartementGateProjectNotFoundException("Departement Gate with " + id + " was not found !"));
        departementGateProject.setDeleted(true);
        departementGateProjectRepository.save(departementGateProject);
    }


    public DepartementGateProjectDto addRequiredActions(DepartementGateProject departementGateProject) {
        Long departementId = departementGateProject.getDepartement().getId();
        Long gateId = departementGateProject.getGateProject().getTrackingGate().getGate().getId();

        ConfigRequiredAction configRequiredAction = configRequiredActionRepository.findByDepartementIdAndGateId(departementId, gateId);
        Set<String> requiredActions = configRequiredAction.getRequiredActions();

        for (String requiredAction : requiredActions){
            RequiredAction requiredAction1 = RequiredAction.builder()
                    .requiredAction(requiredAction)
                    .departementGateProject(departementGateProject)
                    .build();
            requiredActionRepository.save(requiredAction1);
        }

        DepartementGateProject updated = departementGateProjectRepository.findById(departementGateProject.getId())
                .orElseThrow(() -> new RuntimeException("Could not reload DepartementGateProject"));

        DepartementGateProjectDto departementGateProjectDto = departementGateProjectMapper.departementGateProjectToDepartementGateProjectDto(updated);
        departementGateProjectDto.setRequiredActionLabels(requiredActions);

        return departementGateProjectDto;
    }







    public void notifySpocsOnGatesAffectationToDepartment(DepartementGateProjectDto departementGateProjectDto, String departement, String emailSender) {
        logger.info("Retrieving users for department: {}", departement);

        List<UserDto> dtoList = client.getUsersByDepartments(Collections.singletonList(departement));

        if (dtoList.isEmpty()) {
            logger.warn("No users found for department: {}", departement);
        } else {
            logger.info("Successfully retrieved {} users for department: {}", dtoList.size(), departement);
        }

        //Set<String> requiredActions = departementGateProjectDto.getRequiredActionLabels();

        GateProject gateProject = gateProjectRepository.findById(departementGateProjectDto.getGateProjectId()).orElseThrow(
                () -> {
                    logger.error("Gate Project with ID {} not found", departementGateProjectDto.getGateProjectId());
                    return new GateProjectNotFoundException("Gate Project affectation not found!");
                });

        Project project = gateProject.getProject();

        List<DepartmentsGatesAffectationEmailDto> affectationEmailDtos = dtoList.stream()
                .map(dto -> {
                    DepartmentsGatesAffectationEmailDto dtoResult = mapToDepartmentsGatesAffectationEmailDto(dto, project.getTitle(), departementGateProjectDto, emailSender);
                    logger.info("Email DTO for {} {} has required actions: {}",
                            dto.getFirstName(),
                            dto.getLastName(),
                            dtoResult.getDepartmentGateRequiredActions().getRequiredActions());
                    return dtoResult;
                })
                .toList();

        logger.info("Sending message to RabbitMQ with {} items", affectationEmailDtos.size());

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.AFFECTATION_GATES_ROUTING_KEY, affectationEmailDtos);

        logger.info("Published message to RabbitMQ!");
    }




    private DepartmentsGatesAffectationEmailDto mapToDepartmentsGatesAffectationEmailDto(UserDto dto, String projectName, DepartementGateProjectDto departementGateProjectDto, String emailSender) {
        DepartmentsGatesAffectationEmailDto departmentsGatesAffectationEmailDto = new DepartmentsGatesAffectationEmailDto();
        UserDetails details = new UserDetails();
        DepartmentGateRequiredAction departmentGateRequiredAction = new DepartmentGateRequiredAction();

        details.setProjectName(projectName);
        details.setLastName(dto.getLastName());
        details.setFirstName(dto.getFirstName());
        details.setDepartment(dto.getDepartment());
        details.setEmail(dto.getEmail());

        departmentsGatesAffectationEmailDto.setEmailSender(emailSender);
        GateProject gateProject = gateProjectRepository.findById(departementGateProjectDto.getGateProjectId()).orElseThrow(
                () -> new GateProjectNotFoundException("Gate project affectation not found !"));

        Set<String> requiredActionsLabels = departementGateProjectDto.getRequiredActionLabels();

        departmentGateRequiredAction.setGate(String.valueOf(gateProject.getTrackingGate().getGate().getGateType()));
        departmentGateRequiredAction.setRequiredActions(requiredActionsLabels);

        departmentsGatesAffectationEmailDto.setUserDetails(details);
        departmentsGatesAffectationEmailDto.setDepartmentGateRequiredActions(departmentGateRequiredAction);
        return departmentsGatesAffectationEmailDto;
    }



    /*
    public List<UserDto> getUsersByDepartment(String department) {
        return client.getUsersByDepartment(department);
    }

     */


    // Get the gates affected for a department of a project
    public Set<GateProjectFront> getGateProjectsByDepartmentAndProjectId(Long projectId, String department) {
        Set<DepartementGateProject> departmentGateProjects = departementGateProjectRepository
                .findGateProjectsByDepartmentAndProjectId(department, projectId);

        Set<GateProjectFront> gateProjectFronts = new HashSet<>();

        for (DepartementGateProject departementGateProject : departmentGateProjects) {
            GateProject gateProject = departementGateProject.getGateProject();
            Project project = gateProject.getProject();

            GateProjectResponseDto gateProjectResponseDto = gateProjectMapper
                    .gateProjectToGateProjectResponseDto(gateProject);

            GateProjectFront gateProjectFront = GateProjectFront.builder()
                    .projectId(project.getId())
                    .titleProject(project.getTitle())
                    .gate(gateProjectResponseDto.getGate())
                    .currentGate(departementGateProject.getGateProject().isCurrentGate())
                    .requiredActions(departementGateProject.getRequiredActions().stream()
                            .map(RequiredAction::getRequiredAction)
                            .collect(Collectors.toSet()))
                    .build();

            gateProjectFronts.add(gateProjectFront);
        }

        return gateProjectFronts.stream()
                .filter(gate -> gate.isCurrentGate() == true)
                .collect(Collectors.toSet());
    }


    public Set<GateProjectFront> getGateProjectsByDepartment(String department, String username) {
        // Step 1: Get allowed projects of the user
        Set<Project> userProjects = getProjectsOfInterlocutors(username);
        Set<Long> userProjectIds = userProjects.stream()
                .map(Project::getId)
                .collect(Collectors.toSet());

        // Step 2: Fetch department gate projects
        Set<DepartementGateProject> departmentGateProjects = departementGateProjectRepository
                .findGateProjectsByDepartment(department);

        Set<GateProjectFront> gateProjects = new HashSet<>();

        for (DepartementGateProject departementGateProject : departmentGateProjects) {
            GateProject gateProject = departementGateProject.getGateProject();
            Project project = gateProject.getProject();

            // Step 3: Filter by user access
            if (!userProjectIds.contains(project.getId())) {
                continue; // Skip projects the user shouldn't see
            }

            GateProjectResponseDto gateProjectResponseDto = gateProjectMapper
                    .gateProjectToGateProjectResponseDto(gateProject);

            GateProjectFront gateProjectFront = GateProjectFront.builder()
                    .projectId(project.getId())
                    .titleProject(project.getTitle())
                    .gate(gateProjectResponseDto.getGate())
                    .currentGate(departementGateProject.getGateProject().isCurrentGate())
                    .requiredActions(departementGateProject.getRequiredActions().stream()
                            .map(RequiredAction::getRequiredAction)
                            .collect(Collectors.toSet()))
                    .build();

            gateProjects.add(gateProjectFront);
        }

        // Step 4: Return only current gates
        return gateProjects.stream()
                .filter(GateProjectFront::isCurrentGate)
                .collect(Collectors.toSet());
    }




    public Set<Project> getProjectsOfInterlocutors(String username){
        List<Long> projectIds = client.getProjectsOfInterlocutor(username).getBody();
        Set<Project> projects = new HashSet<>();
        for (Long id : projectIds){
            Project project = projectRepository.findById(id).orElseThrow(
                    () -> new ProjectNotFoundException("Project with ID : " + id + " was not found !"));
            projects.add(project);
        }
        return projects;
    }


    public Set<Project> getProjectsOfSpoc(String department) {
        List<Long> projectIds = client.getProjectsReservedForSpoc(department).getBody();
        Set<Project> projects = new HashSet<>();

        for (Long id : projectIds) {
            Project project = projectRepository.findById(id).orElseThrow(
                    () -> new ProjectNotFoundException("Project with ID : " + id + " was not found !"));

            logger.info("Fetched project title: {}", project.getTitle());
            projects.add(project);
        }

        return projects;
    }



    public Set<GateProjectFront> getGateProjectsSpocByDepartment(String department) {

        Set<Project> userProjects = getProjectsOfSpoc(department);
        Set<Long> userProjectIds = userProjects.stream()
                .map(Project::getId)
                .collect(Collectors.toSet());

        // Step 2: Fetch department gate projects
        Set<DepartementGateProject> departmentGateProjects = departementGateProjectRepository
                .findGateProjectsByDepartment(department);

        Set<GateProjectFront> gateProjects = new HashSet<>();

        for (DepartementGateProject departementGateProject : departmentGateProjects) {
            GateProject gateProject = departementGateProject.getGateProject();
            Project project = gateProject.getProject();

            // Step 3: Filter by user access
            if (!userProjectIds.contains(project.getId())) {
                continue; // Skip projects the user shouldn't see
            }

            GateProjectResponseDto gateProjectResponseDto = gateProjectMapper
                    .gateProjectToGateProjectResponseDto(gateProject);

            GateProjectFront gateProjectFront = GateProjectFront.builder()
                    .projectId(project.getId())
                    .titleProject(project.getTitle())
                    .gate(gateProjectResponseDto.getGate())
                    .currentGate(departementGateProject.getGateProject().isCurrentGate())
                    .requiredActions(departementGateProject.getRequiredActions().stream()
                            .map(RequiredAction::getRequiredAction)
                            .collect(Collectors.toSet()))
                    .build();

            gateProjects.add(gateProjectFront);
        }

        // Step 4: Return only current gates
        return gateProjects.stream()
                .filter(GateProjectFront::isCurrentGate)
                .collect(Collectors.toSet());
    }




    @Transactional
    public Set<DepartementGateProjectDto> affectGateProjectsToDepartment(Set<Long> gateProjectIds, Long departmentId, String emailSender) {
        Set<DepartementGateProjectDto> departmentGateProjectDtos = new HashSet<>();

        for (Long gateProjectId : gateProjectIds){
            DepartementGateProjectDto departementGateProjectDto = affectGateProjectToDepartement(gateProjectId, departmentId, emailSender);
            departmentGateProjectDtos.add(departementGateProjectDto);
        }

        return departmentGateProjectDtos;
    }

}
