package ma.inwi.msproject.service;

import jakarta.transaction.Transactional;
import ma.inwi.msproject.client.ProjectRestClient;
import ma.inwi.msproject.client.UserClient;
import ma.inwi.msproject.configuration.RabbitMQConfig;
import ma.inwi.msproject.dto.*;
import ma.inwi.msproject.dto.notifications.EndProjectMailDto;
import ma.inwi.msproject.dto.notifications.NewProjectCreationMailDto;
import ma.inwi.msproject.dto.notifications.NextGateTransitionMailDto;
import ma.inwi.msproject.dto.notifications.ProjectAffectedToInterlocutorImpactMailDto;
import ma.inwi.msproject.entities.*;
import ma.inwi.msproject.enums.DecisionType;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.enums.StatusProjectAssignement;
import ma.inwi.msproject.exceptions.*;
import ma.inwi.msproject.mappers.GateProjectMapper;
import ma.inwi.msproject.mappers.ProjectMapper;
import ma.inwi.msproject.mappers.TrackingGateMapper;
import ma.inwi.msproject.repositories.GateProjectRepository;
import ma.inwi.msproject.repositories.ProjectRepository;
import ma.inwi.msproject.repositories.TrackingGateRepository;
import ma.inwi.msproject.repositories.TrackingRepository;
import ma.inwi.msproject.repositories.spec.ProjectSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final GateProjectRepository gateProjectRepository;
    private final TrackingRepository trackingRepository;
    private final TrackingGateService trackingGateService;
    private final TrackingGateMapper trackingGateMapper;
    private final TrackingGateRepository trackingGateRepository;
    private final GateProjectMapper gateProjectMapper;

    private final UserClient client;
    private final RabbitTemplate rabbitTemplate;


    //Feign Clients
    private ProjectRestClient projectRestClient;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          ProjectMapper projectMapper,
                          GateProjectRepository gateProjectRepository,
                          TrackingRepository trackingRepository,
                          GateProjectMapper gateProjectMapper,
                          TrackingGateRepository trackingGateRepository,
                          TrackingGateService trackingGateService,
                          TrackingGateMapper trackingGateMapper,
                          ProjectRestClient projectRestClient,
                          UserClient client,
                          RabbitTemplate rabbitTemplate) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.gateProjectRepository = gateProjectRepository;
        this.trackingRepository = trackingRepository;
        this.gateProjectMapper = gateProjectMapper;
        this.trackingGateRepository = trackingGateRepository;
        this.trackingGateService = trackingGateService;
        this.trackingGateMapper = trackingGateMapper;
        this.projectRestClient = projectRestClient;
        this.rabbitTemplate = rabbitTemplate;

        this.client = client;
    }


    public DocumentDto uploadFile(MultipartFile file){
        return projectRestClient.uploadFile(file).getBody();
    }



    @Transactional
    public ProjectDto addProject(ProjectDto projectRequest) {

        Optional<Project> projectOptional = projectRepository.findByTitle(projectRequest.getTitle());

        if (projectOptional.isPresent()){
            throw new ProjectAlreadyExistingException("The project with ID " + projectOptional.get().getId() + " already exists !");
        }

        logger.info("Starting addProject operation for trackingId: {}", projectRequest.getTrackingId());

        Tracking tracking = trackingRepository.findById(projectRequest.getTrackingId())
                .orElseThrow(() -> new TrackingNotFoundException(
                        "Tracking with id " + projectRequest.getTrackingId() + " was not found!"));
        logger.info("Tracking found: {}", tracking.getId());

        Set<TrackingGateDto> trackingGatesDtos = trackingGateService.getRelatedGatesToTracking(tracking.getId());
        logger.info("Retrieved {} tracking gates for trackingId: {}", trackingGatesDtos.size(), tracking.getId());

        Set<TrackingGate> trackingGates = trackingGatesDtos.stream()
                .map(trackingGateMapper::trackingGateDtoToTrackingGate)
                .collect(Collectors.toSet());
        logger.info("Converted tracking gates to entity objects.");

        projectRequest.setDateCreation(LocalDateTime.now());

        Project project = projectMapper.projectDtoToProject(projectRequest);

        project = projectRepository.save(project);
        logger.info("Project saved with ID: {}", project.getId());

        Set<GateProject> gateProjects = new HashSet<>();
        Project finalProject = project;

        for (TrackingGate trackingGate : trackingGates){
            logger.info("Creating GateProject for trackingGateId: {}", trackingGate.getId());
            GateProject gateProject = GateProject.builder()
                    .trackingGate(trackingGate)
                    .inProgress(true)
                    .project(finalProject)
                    .build();
            gateProjects.add(gateProject);
        }

        logger.info("Gate project creation finished. Created {} GateProjects.", gateProjects.size());

        boolean currentGateSet = false;
        for (GateProject gateProject : gateProjects) {
            logger.info("Checking GateProject ID: {} with gateBefore: {}", gateProject.getId(),
                    gateProject.getTrackingGate().getGateBefore());

            if (gateProject.getTrackingGate().getGateBefore().getId() == null) {
                gateProject.setCurrentGate(true);
                gateProject.setStartingGate(true);
                currentGateSet = true;
                logger.info("GateProject ID: {} set as current gate for project ID: {}", gateProject.getId(), project.getId());
                break;
            }
        }

        if (!currentGateSet) {
            logger.warn("No GateProject found with a null 'gateBefore'. Current gate not set for project ID: {}", project.getId());
        }


        //List<GateProject> savedGateProjects = gateProjectRepository.saveAll(gateProjects);

        Set<GateProject> savedGateProjects = new HashSet<>();
        for (GateProject gateProject : gateProjects){
            gateProjectRepository.save(gateProject);
            savedGateProjects.add(gateProject);
        }

        logger.info("Saved {} GateProjects to database for project ID: {}", savedGateProjects.size(), project.getId());

        Set<GateProject> gateProjects1 = new HashSet<>(savedGateProjects);
        project.setGateProjects(gateProjects1);

        project = projectRepository.save(project);
        logger.info("Final project update completed for project ID: {}", project.getId());

        logger.info("addProject operation completed successfully for project ID: {}", project.getId());

        logger.info("The ownerFull name is {}", project.getOwnerFullName());
        notifySpocsOnceProjectCreated(projectMapper.projectToProjectDto(project));

        logger.info("Message successfully produced");

        return projectMapper.projectToProjectDto(project);
    }


    /*
    public void setProjectCurrentGateWhenCreated(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("The project with id " + projectId + " was not found!"));

        Set<GateProject> gateProjects = gateProjectRepository.findGateProjectsByProjectId(projectId);
        logger.info("Retrieved {} GateProjects for project ID: {}", gateProjects.size(), projectId);

        for (GateProject gateProject : gateProjects) {
            if (gateProject.getTrackingGate().getGateBefore() == null) {
                gateProject.setCurrentGate(true);
                logger.info("GateProject ID: {} set as current gate for project ID: {}", gateProject.getId(), projectId);
                break;  // Assuming only one GateProject should be set as the current gate
            }
        }

        gateProjectRepository.saveAll(gateProjects);
        logger.info("Updated GateProjects saved for project ID: {}", projectId);
    }

     */


    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID " + id + " was not found ! "));
        if (project.isDeleted()){
            return null;
        }
        return projectMapper.projectToProjectDto(project);
    }

    public Set<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> !project.isDeleted())
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }


    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto updatedProject) {
        Project existingProject = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID " + id + " was not found ! ")
        );
        if (existingProject.isDeleted()){
            return null;
        }

        existingProject.setTitle(updatedProject.getTitle());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setMarketType(updatedProject.getMarketType());
        existingProject.setProjectType(updatedProject.getProjectType());
        existingProject.setTtmComitteeSubCategory(updatedProject.getTtmComitteeSubCategory());
        existingProject.setSubcategoryCommercialCodir(updatedProject.getSubcategoryCommercialCodir());
        existingProject.setIsConfidential(updatedProject.getIsConfidential());
        existingProject.setDateStartTtm(updatedProject.getDateStartTtm());
        existingProject.setComments(updatedProject.getComments());
        existingProject.setMoas(updatedProject.getMoas());

        existingProject.setExpressionOfNeed(updatedProject.getExpressionOfNeed());
        existingProject.setBriefCommunication(updatedProject.getBriefCommunication());
        existingProject.setBriefCDG(updatedProject.getBriefCDG());
        existingProject.setRegulatoryBrief(updatedProject.getRegulatoryBrief());
        existingProject.setAttachedDocuments(updatedProject.getAttachedDocuments());


        projectRepository.save(existingProject);
        return projectMapper.projectToProjectDto(existingProject);
    }

    /*
    @Transactional
    public void goToNextGateProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("The project with id " + projectId + " was not found!"));

        if (project.isDeleted()) {
            return;
        }

        Gate nextGate = null;
        GateProject passedGateProject = null;

        for (GateProject gateProject : project.getGateProjects()) {
            if (gateProject.isCurrentGate()) {
                if (gateProject.getDecisionType() == DecisionType.NO_GO || gateProject.getDecisionType() == null) {
                    throw new UnauthorizedPassageToNextGateException("You are not allowed to move to the next gate!");
                }

                gateProject.setCurrentGate(false);
                gateProject.setPassingDate(LocalDateTime.now());
                passedGateProject = gateProject;

                if (gateProject.getTrackingGate().getGateAfter() == null) {
                    throw new ReachedFinalGateException("The project has reached its final gate and cannot proceed further.");
                }

                nextGate = gateProject.getTrackingGate().getGateAfter();
                break;
            }
        }

        if (nextGate == null) {
            return;
        }

        for (GateProject gateProject : project.getGateProjects()) {
            if (gateProject.getTrackingGate().getGate().equals(nextGate)) {
                gateProject.setCurrentGate(true);
                break;
            }
        }

        projectRepository.save(project);

        notifyUsersOnceProjectPassedToNextGate(project, passedGateProject, nextGate.getGateType());
    }

     */


    public void goToNextGateProject(Long projectId, String ownerEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("The project with ID " + projectId + " was not found!"));

        if (project.isDeleted()) {
            return;
        }

        // Reload gateProjects directly from DB
        List<GateProject> gateProjects = gateProjectRepository.findByProjectId(projectId);

        Gate nextGate = null;
        GateProject passedGateProject = null;

        for (GateProject gateProject : gateProjects) {

            if (gateProject.isCurrentGate()) {
                if (gateProject.getDecisionType() == DecisionType.NO_GO
                        || gateProject.getDecisionType() == null
                        || !gateProject.isInProgress()) {
                    throw new UnauthorizedPassageToNextGateException("You are not allowed to move to the next gate!");
                }

                gateProject.setCurrentGate(false);
                gateProject.setPassingDate(LocalDateTime.now());
                passedGateProject = gateProject;

                // If it's the final gate
                if (gateProject.getTrackingGate().getGateAfter() == null) {
                    gateProject.setProjectCompleted(true);
                    gateProject.setCurrentGate(true);

                    logger.info("The projectCompleted value is: {}", gateProject.isProjectCompleted());

                    gateProjectRepository.save(gateProject);
                    //gateProjectRepository.flush();

                    notifyEndOfProject(project, passedGateProject, ownerEmail);

                    throw new ReachedFinalGateException("The project has reached its final gate and cannot proceed further.");
                }


                nextGate = gateProject.getTrackingGate().getGateAfter();
                break;
            }
        }

        if (nextGate == null) {
            return;
        }

        for (GateProject gateProject : gateProjects) {
            if (gateProject.getTrackingGate().getGate().equals(nextGate)) {
                gateProject.setCurrentGate(true);
                break;
            }
        }

        gateProjectRepository.saveAll(gateProjects);

        notifyUsersOnceProjectPassedToNextGate(project, passedGateProject, nextGate.getGateType(), ownerEmail);
    }




    /*
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID " + id + " was not found !"));
        project.setDeleted(true);
        projectRepository.save(project);
    }

     */

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID " + id + " was not found !"));
        projectRepository.delete(project);
    }





    //Notifications
    public void notifyUsersOnceProjectPassedToNextGate(Project project, GateProject passedGate, GateType nextGate, String ownerEmail){
        //List<UserDto> dtoList = client.getUsersByDepartments(project.getDepartments());
        List<UserDto> dtoList = client.getUsersOfAllDepartments(project.getDepartments());

        List<NextGateTransitionMailDto> usersList = dtoList.stream()
                .map(dto -> mapToNextGateTransitionMailDto(dto, project.getTitle(), passedGate, nextGate, ownerEmail))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.NEXT_GATE_ROUTING_KEY, usersList);
    }

    /*
    public void notifyEndOfProject(Project project, GateProject passedGate){
        //List<UserDto> dtoList = client.getUsersByDepartments(project.getDepartments());
        List<UserDto> dtoList = client.getUsersOfAllDepartments(project.getDepartments());

        List<NextGateTransitionMailDto> usersList = dtoList.stream()
                .map(dto -> mapToEndProjectDto(dto, project.getTitle(), passedGate))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.NEXT_GATE_ROUTING_KEY, usersList);
    }

     */

    public void notifyEndOfProject(Project project, GateProject passedGate, String ownerEmail){
        //List<UserDto> dtoList = client.getUsersByDepartments(project.getDepartments());
        List<UserDto> dtoList = client.getUsersOfAllDepartments(project.getDepartments());

        List<NextGateTransitionMailDto> usersList = dtoList.stream()
                .map(dto -> mapToEndProjectDto(dto, project.getId(), passedGate, ownerEmail))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.NEXT_GATE_ROUTING_KEY, usersList);
    }



    /*
    public void notifySpocsOnceProjectCreated(ProjectDto projectDto){
        List<UserDto> dtoList = client.getUsersByDepartments(projectDto.getDepartments());

        List<UserDetails> usersList = dtoList.stream()
                .map(dto -> mapToUserDetails(dto, projectDto.getTitle()))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, usersList);
    }

     */


    public void notifySpocsOnceProjectCreated(ProjectDto projectDto){
        List<UserDto> dtoList = client.getUsersByDepartments(projectDto.getDepartments());

        List<NewProjectCreationMailDto> usersList = dtoList.stream()
                .map(dto -> mapToNewProjectCreationMailDto(dto, projectDto))
                .toList();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, usersList);
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

    public NewProjectCreationMailDto mapToNewProjectCreationMailDto(UserDto dto, ProjectDto projectDto) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        NewProjectCreationMailDto mailDto = new NewProjectCreationMailDto();

        UserDetails userDetails = mapToUserDetails(dto, projectDto.getTitle());
        mailDto.setUserDetails(userDetails);

        mailDto.setOwnerName(projectDto.getOwnerFullName());
        mailDto.setDescription(projectDto.getDescription());
        mailDto.setMarketType(projectDto.getMarketType());
        mailDto.setProjectType(projectDto.getProjectType());
        mailDto.setTtmComitteeSubCategory(projectDto.getTtmComitteeSubCategory());
        mailDto.setSubcategoryCommercialCodir(projectDto.getSubcategoryCommercialCodir());

        mailDto.setConfidential(Boolean.TRUE.equals(projectDto.getIsConfidential()) ? "Confidentiel" : "Non confidentiel");

        if (projectDto.getDateStartTtm() != null) {
            mailDto.setDateStartTtm(formatter.format(projectDto.getDateStartTtm()));
        } else {
            mailDto.setDateStartTtm("Date non renseignée");
        }

        return mailDto;
    }



    private NextGateTransitionMailDto mapToNextGateTransitionMailDto(UserDto dto, String projectName, GateProject passedGate, GateType futureGate, String ownerEmail){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NextGateTransitionMailDto nextGateTransitionMailDto = new NextGateTransitionMailDto();
        UserDetails userDetails = mapToUserDetails(dto, projectName);
        nextGateTransitionMailDto.setUserDetails(userDetails);
        nextGateTransitionMailDto.setFutureGate(futureGate);
        nextGateTransitionMailDto.setPassedGate(passedGate.getTrackingGate().getGate().getGateType());
        nextGateTransitionMailDto.setInformation(passedGate.getInformation());
        nextGateTransitionMailDto.setDecisions(passedGate.getDecisions());
        nextGateTransitionMailDto.setActions(passedGate.getActions());
        nextGateTransitionMailDto.setPassingDate(formatter.format(passedGate.getPassingDate()));

        nextGateTransitionMailDto.setEmailSender(ownerEmail);

        return nextGateTransitionMailDto;
    }

    /*
    private NextGateTransitionMailDto mapToEndProjectDto(UserDto dto, String projectName, GateProject passedGate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NextGateTransitionMailDto nextGateTransitionMailDto = new NextGateTransitionMailDto();
        UserDetails userDetails = mapToUserDetails(dto, projectName);
        nextGateTransitionMailDto.setUserDetails(userDetails);
        nextGateTransitionMailDto.setFutureGate(null);
        nextGateTransitionMailDto.setPassedGate(passedGate.getTrackingGate().getGate().getGateType());
        nextGateTransitionMailDto.setInformation(passedGate.getInformation());
        nextGateTransitionMailDto.setDecisions(passedGate.getDecisions());
        nextGateTransitionMailDto.setActions(passedGate.getActions());
        nextGateTransitionMailDto.setPassingDate(formatter.format(passedGate.getPassingDate()));

        return nextGateTransitionMailDto;
    }

     */

    private NextGateTransitionMailDto mapToEndProjectDto(UserDto dto, Long projectId, GateProject passedGate, String ownerEmail){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID : " + projectId + " was not found !"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NextGateTransitionMailDto nextGateTransitionMailDto = new NextGateTransitionMailDto();
        UserDetails userDetails = mapToUserDetails(dto, project.getTitle());
        nextGateTransitionMailDto.setUserDetails(userDetails);
        nextGateTransitionMailDto.setFutureGate(null);
        nextGateTransitionMailDto.setPassedGate(passedGate.getTrackingGate().getGate().getGateType());
        nextGateTransitionMailDto.setInformation(passedGate.getInformation());
        nextGateTransitionMailDto.setDecisions(passedGate.getDecisions());
        nextGateTransitionMailDto.setActions(passedGate.getActions());
        nextGateTransitionMailDto.setPassingDate(formatter.format(passedGate.getPassingDate()));
        nextGateTransitionMailDto.setDaysTtm(calculateTtm(projectId));

        nextGateTransitionMailDto.setEmailSender(ownerEmail);

        return nextGateTransitionMailDto;
    }


    public List<UserDto> getUserByDepartment(List<String> departments) {
        return client.getUsersByDepartments(departments);
    }


    public String affectProjectToInterlocutorSignalingImpact(String spoc, String usernameInterlocutor, Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID : " + projectId + " was not found !"));

        String affectationMessage = client.affectProjectToInterlocutor(usernameInterlocutor, projectId);
        UserDto userDto = client.getUserByUsername(usernameInterlocutor);
        notifyInterlocutorSignalingImpactOnProjectAffectation(spoc, userDto, project.getTitle());

        return affectationMessage;
    }


    private void notifyInterlocutorSignalingImpactOnProjectAffectation(String spoc, UserDto userDto, String title) {
        UserDetails userDetails = mapToUserDetails(userDto, title);
        ProjectAffectedToInterlocutorImpactMailDto projectAffectedToInterlocutorImpactMailDto = ProjectAffectedToInterlocutorImpactMailDto.builder()
                .senderMail(spoc)
                .userDetails(userDetails)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.AFFECTATION_IMPACT_ROUTING_KEY, projectAffectedToInterlocutorImpactMailDto);
    }




    public String affectProjectToInterlocutorRespondingImpact(String spoc, String usernameInterlocutor, Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID : " + projectId + " was not found !"));

        String affectationMessage = client.affectProjectToInterlocutor(usernameInterlocutor, projectId);
        UserDto userDto = client.getUserByUsername(usernameInterlocutor);
        notifyInterlocutorRespondingImpactOnProjectAffectation(spoc, userDto, project.getTitle());

        return affectationMessage;
    }


    private void notifyInterlocutorRespondingImpactOnProjectAffectation(String spoc, UserDto userDto, String title) {
        UserDetails userDetails = mapToUserDetails(userDto, title);
        ProjectAffectedToInterlocutorImpactMailDto projectAffectedToInterlocutorImpactMailDto = ProjectAffectedToInterlocutorImpactMailDto.builder()
                .senderMail(spoc)
                .userDetails(userDetails)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_ROUTING_KEY, projectAffectedToInterlocutorImpactMailDto);
    }

    public Set<ProjectDto> getProjectsByOwnerUsername(String ownerUsername){
        List<Project> projects = projectRepository.findProjectsByOwnerUsername(ownerUsername);
        if (projects.isEmpty()){
            throw new ProjectNotFoundException("No projects were found !");
        }
        return projects.stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }


    public Long calculateTimeBetweenTwoGates(GateProject gate1, GateProject gate2){

        LocalDateTime passingDateGate1 = gate1.getPassingDate();
        LocalDateTime passingDateGate2 = gate2.getPassingDate();

        LocalDate initialDate1 = LocalDate.of(passingDateGate1.getYear(), passingDateGate1.getMonthValue(), passingDateGate1.getDayOfMonth());
        LocalDate initialDate2 = LocalDate.of(passingDateGate2.getYear(), passingDateGate2.getMonthValue(), passingDateGate2.getDayOfMonth());

        return ChronoUnit.DAYS.between(initialDate1, initialDate2);
    }


    public Long calculateTtm(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("Project with ID : " + projectId + " was not found !"));

        List<GateProject> gateProjects = projectRepository.findGateProjectsByProjectId(projectId);
        if (gateProjects.isEmpty()) {
            throw new GateProjectNotFoundException("No concrete gate is found!");
        }

        Date dateStartTtm = project.getDateStartTtm();
        if (dateStartTtm == null) {
            throw new IllegalStateException("Start date for TTM is not set.");
        }

        LocalDate start = dateStartTtm.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime gate1 = gateProjects.getFirst().getPassingDate();
        LocalDate firstGate = LocalDate.of(gate1.getYear(), gate1.getMonthValue(), gate1.getDayOfMonth());

        Long sum = ChronoUnit.DAYS.between(start, firstGate);

        for (int i = 0; i < gateProjects.size() - 1; i++) {
            sum += calculateTimeBetweenTwoGates(gateProjects.get(i), gateProjects.get(i + 1));
        }

        return sum;
    }



    public Set<ProjectDto> filterProjects(String projectType, String marketType) {
        Specification<Project> spec = Specification.where(null);

        if (projectType != null && !projectType.isEmpty()) {
            spec = spec.and(ProjectSpec.hasProjectType(projectType));
        }

        if (marketType != null && !marketType.isEmpty()) {
            spec = spec.and(ProjectSpec.hasMarketType(marketType));
        }

        return projectRepository.findAll(spec).stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }



    public Set<ProjectFrontDto> filterProjectsForSpoc(String department, String projectType, String marketType) {
        Specification<Project> spec = Specification.where(null);

        // Build the specification dynamically based on provided filters
        if (projectType != null && !projectType.isEmpty()) {
            spec = spec.and(ProjectSpec.hasProjectType(projectType));
        }

        if (marketType != null && !marketType.isEmpty()) {
            spec = spec.and(ProjectSpec.hasMarketType(marketType));
        }

        // Get the list of ProjectDtos
        Set<ProjectDto> projectDtos = projectRepository.findAll(spec).stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());

        // Get the list of ProjectFrontDtos
        Set<ProjectFrontDto> projectFrontDtos = getProjectsForSpoc(department);

        // Create a set of project IDs from projectDtos
        Set<Long> projectDtoIds = projectDtos.stream()
                .map(ProjectDto::getId)  // Assuming ProjectDto has getId() method
                .collect(Collectors.toSet());

        // Filter projectFrontDtos to keep only those with IDs that are in projectDtos
        Set<ProjectFrontDto> filteredProjectFrontDtos = projectFrontDtos.stream()
                .filter(frontDto -> projectDtoIds.contains(frontDto.getId()))  // Assuming ProjectFrontDto has getId() method
                .collect(Collectors.toSet());

        // Return the filtered set of ProjectFrontDto
        return filteredProjectFrontDtos;
    }








    public Set<ProjectDto> getProjectsNotAffected(String department) {
        // Get interlocutors and their affected projectsttm-iam-db
        Set<UserDtoRs> userDtoRs = client.getInterlocutorsByDepartment(department);
        List<Project> allProjectsDepartment = projectRepository.findProjectsByDepartment(department);

        // IDs of projects already assigned to interlocutors
        Set<Long> affectedProjectIds = userDtoRs.stream()
                .flatMap(user -> user.getProjectsId().stream())
                .collect(Collectors.toSet());

        // Get reserved projects for SPOC and collect their IDs
        Set<Long> reservedSpocProjectIds = client.getProjectsReservedForSpoc(department).getBody()
                .stream()
                .collect(Collectors.toSet());

        // Filter out affected and reserved projects
        Set<Project> notAffectedProjects = allProjectsDepartment.stream()
                .filter(project -> !affectedProjectIds.contains(project.getId()))
                .filter(project -> !reservedSpocProjectIds.contains(project.getId()))
                .collect(Collectors.toSet());

        return notAffectedProjects.stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }



    public Set<ProjectDto> getAffectedProjects(String department) {
        Set<ProjectDto> allProjects = getAllProjects();

        Set<ProjectDto> notAffected = getProjectsNotAffected(department);
        Set<ProjectDto> reservedBySpoc = getProjectsReservedBySpoc(department);

        // Remove both not affected and reserved projects
        allProjects.removeAll(notAffected);
        allProjects.removeAll(reservedBySpoc);

        return allProjects;
    }



    public Set<ProjectDto> getProjectsReservedBySpoc(String department) {
        List<Long> spocProjectIds = client.getProjectsReservedForSpoc(department).getBody();

        Set<Project> projects = new HashSet<>();
        for (Long id : spocProjectIds) {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + id + " was not found"));
            projects.add(project);
        }

        return projects.stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }



    public Set<ProjectFrontDto> getProjectsForSpoc(String department) {
        Set<ProjectDto> affectedProjects = getAffectedProjects(department);
        Set<ProjectDto> notAffectedProjects = getProjectsNotAffected(department);
        Set<ProjectDto> reservedProjectsForSpoc = getProjectsReservedBySpoc(department);

        // Map affected projects to ProjectFrontDto with assignedToInterlocutors = true
        Set<ProjectFrontDto> affectedFrontDtos = affectedProjects.stream()
                .map(dto -> mapToFrontDto(dto, StatusProjectAssignement.ASSIGNED))
                .collect(Collectors.toSet());

        Set<ProjectFrontDto> notAffectedFrontDtos = notAffectedProjects.stream()
                .map(dto -> mapToFrontDto(dto, StatusProjectAssignement.NOT_ASSIGNED))
                .collect(Collectors.toSet());

        Set<ProjectFrontDto> reservedSpocDtos = reservedProjectsForSpoc.stream()
                .map(dto -> mapToFrontDto(dto, StatusProjectAssignement.RESERVED))
                .collect(Collectors.toSet());

        Set<ProjectFrontDto> allFrontDtos = new HashSet<>();
        allFrontDtos.addAll(affectedFrontDtos);
        allFrontDtos.addAll(notAffectedFrontDtos);
        allFrontDtos.addAll(reservedSpocDtos);

        return allFrontDtos;
    }

    /*
    public Set<UserDto> getInterlocutorsSignalingImpactByDepartment(String department){
        List<UserDto> users = client.getUsersByDepartments(Collections.singletonList(department));
        return users.stream()
                .filter(userDto -> userDto.getRoles().contains("INTERLOCUTEUR_SIGNALE_IMPACT"))
                .collect(Collectors.toSet());
    }

    public Set<UserDto> getInterlocutorsRespondingImpactByDepartment(String department){
        List<UserDto> users = client.getUsersByDepartments(Collections.singletonList(department));
        return users.stream()
                .filter(userDto -> userDto.getRoles().contains("INTERLOCUTEUR_RETOUR_IMPACT"))
                .collect(Collectors.toSet());
    }
     */

    public Set<UserDto> getInterlocutorsSignalingImpactByDepartment(String department){
        List<UserDto> users = client.getUsersByDepartment(department).getBody();
        return users.stream()
                .filter(userDto -> userDto.getRoles().contains("INTERLOCUTEUR_SIGNALE_IMPACT"))
                .collect(Collectors.toSet());
    }

    public Set<UserDto> getInterlocutorsRespondingImpactByDepartment(String department){
        List<UserDto> users = client.getUsersByDepartment(department).getBody();
        return users.stream()
                .filter(userDto -> userDto.getRoles().contains("INTERLOCUTEUR_RETOUR_IMPACT"))
                .collect(Collectors.toSet());
    }



    public Gate getNextGateProject(Long projectId) {
        logger.info("Fetching next gate for project with ID: {}", projectId);

        List<GateProject> gateProjects = projectRepository.findGateProjectsByProjectId(projectId);
        if (gateProjects.isEmpty()) {
            logger.warn("No gate projects found for project ID: {}", projectId);
            throw new ProjectNotFoundException("No project was found!");
        }

        for (GateProject gateProject : gateProjects) {
            logger.debug("Inspecting GateProject: {}", gateProject);

            if (gateProject.isCurrentGate()) {
                Gate nextGate = gateProject.getTrackingGate().getGateAfter();

                if (nextGate == null) {
                    if (!gateProject.isProjectCompleted()) {
                        logger.error("First Case - Next gate does not exist for current gate in project ID: {}", projectId);
                        throw new CaseOneNextGateNotExistantException("First Case - Next gate does not exist for the current gate!");
                    } else {
                        logger.error("Second Case - Next gate does not exist for current gate in project ID: {}", projectId);
                        throw new SecondCaseNextGateNotExistantException("Second Case - Next gate does not exist for the current gate!");
                    }
                }

                return nextGate;
            }
        }

        logger.warn("No current gate marked in project ID: {}", projectId);
        return null;
    }





    private ProjectFrontDto mapToFrontDto(ProjectDto dto, StatusProjectAssignement assigned) {
        ProjectFrontDto frontDto = new ProjectFrontDto();
        frontDto.setId(dto.getId());
        frontDto.setOwnerUsername(dto.getOwnerUsername());
        frontDto.setOwnerFullName(dto.getOwnerFullName());
        frontDto.setTitle(dto.getTitle());
        frontDto.setDescription(dto.getDescription());
        frontDto.setMarketType(dto.getMarketType());
        frontDto.setProjectType(dto.getProjectType());
        frontDto.setTtmComitteeSubCategory(dto.getTtmComitteeSubCategory());
        frontDto.setSubcategoryCommercialCodir(dto.getSubcategoryCommercialCodir());
        frontDto.setIsConfidential(dto.getIsConfidential());
        frontDto.setDateStartTtm(dto.getDateStartTtm());
        frontDto.setExpressionOfNeed(dto.getExpressionOfNeed());
        frontDto.setBriefCommunication(dto.getBriefCommunication());
        frontDto.setBriefCDG(dto.getBriefCDG());
        frontDto.setRegulatoryBrief(dto.getRegulatoryBrief());
        frontDto.setAttachedDocuments(dto.getAttachedDocuments());
        frontDto.setComments(dto.getComments());
        frontDto.setDateCreation(dto.getDateCreation());
        frontDto.setMoas(dto.getMoas());
        frontDto.setTrackingId(dto.getTrackingId());
        frontDto.setGateProjectIds(dto.getGateProjectIds());
        frontDto.setCurrentGate(dto.getCurrentGate());
        frontDto.setRequiredActions(dto.getRequiredActions());
        frontDto.setDepartments(dto.getDepartments());
        frontDto.setAssignedToInterlocutors(assigned);
        return frontDto;
    }




}


