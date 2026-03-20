package ma.inwi.msproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import ma.inwi.msproject.client.ProjectRestClient;
import ma.inwi.msproject.dto.DocumentDto;
import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.dto.ProjectFrontDto;
import ma.inwi.msproject.dto.UserDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.ProjectNotFoundException;
import ma.inwi.msproject.exceptions.ReachedFinalGateException;
import ma.inwi.msproject.exceptions.UnauthorizedPassageToNextGateException;
import ma.inwi.msproject.exceptions.UserNotFoundException;
import ma.inwi.msproject.repositories.ProjectRepository;
import ma.inwi.msproject.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/project")
@CrossOrigin("*")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private final ProjectService projectService;

    private final ProjectRepository projectRepository;
    private final ProjectRestClient projectRestClient;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository, ProjectRestClient projectRestClient) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.projectRestClient = projectRestClient;
    }


    @PostMapping("/add")
    @PreAuthorize(value = "hasAuthority('OWNER')")
    public ResponseEntity<ProjectDto> addProject(MultipartHttpServletRequest request, JwtAuthenticationToken jwt) throws JsonProcessingException {
        // Initialize logger

        // Log incoming request
        logger.info("Received request to add project");

        // Extracting files from the request
        MultipartFile expressionOfNeed = request.getFile("expressionOfNeed");
        MultipartFile briefCommunication = request.getFile("briefCommunication");
        MultipartFile briefCDG = request.getFile("briefCDG");
        MultipartFile regulatoryBrief = request.getFile("regulatoryBrief");
        MultipartFile[] attachedDocuments = request.getFiles("attachedDocuments[]").toArray(new MultipartFile[0]);

        // Extracting projectDto parameter from the request
        String projectDtoParam = request.getParameter("projectDto");
        if (projectDtoParam == null || projectDtoParam.isEmpty()) {
            logger.error("ProjectDto parameter is missing or empty");
            throw new IllegalArgumentException("ProjectDto parameter is missing or empty");
        }

        // Log the incoming projectDto JSON
        logger.info("Received projectDto: {}", projectDtoParam);

        // Convert projectDtoParam to ProjectDto object
        ProjectDto projectDto = new ObjectMapper().readValue(projectDtoParam, ProjectDto.class);

        // Extract ownerUsername from JWT
        String owner = jwt.getToken().getClaim("preferred_username");
        logger.info("Extracted ownerUsername: {}", owner); // Log the extracted ownerUsername

        String ownerFirstName = jwt.getToken().getClaim("given_name");
        String ownerLastName = jwt.getToken().getClaim("family_name");


        projectDto.setOwnerFullName(ownerFirstName + " " + ownerLastName);

        // If ownerUsername is null or empty, log an error
        if (owner == null || owner.isEmpty()) {
            logger.error("Owner username is null or empty");
        }

        // Set the ownerUsername in the projectDto
        projectDto.setOwnerUsername(owner);

        // Log the projectDto with owner set
        logger.info("ProjectDto with owner set: {}", projectDto);

        // Upload the files

        DocumentDto documentExpressionOfNeedName = projectService.uploadFile(expressionOfNeed);
        DocumentDto documentBriefCommunicationName = projectService.uploadFile(briefCommunication);
        DocumentDto documentBriefCDGName = projectService.uploadFile(briefCDG);
        DocumentDto documentRegulatoryBriefName = projectService.uploadFile(regulatoryBrief);


        // Log file uploads
        logger.info("Uploaded file expressions: {} | {} | {} | {}",
                documentExpressionOfNeedName.getDocumentLabel(),
                documentBriefCommunicationName.getDocumentLabel(),
                documentBriefCDGName.getDocumentLabel(),
                documentRegulatoryBriefName.getDocumentLabel()
        );

        // Extract document labels
        String expressionOfNeedName = documentExpressionOfNeedName.getDocumentLabel();
        String briefCommunicationName = documentBriefCommunicationName.getDocumentLabel();
        String briefCDGName = documentBriefCDGName.getDocumentLabel();
        String regulatoryBriefName = documentRegulatoryBriefName.getDocumentLabel();

        // Set document labels in projectDto
        projectDto.setExpressionOfNeed(expressionOfNeedName);
        projectDto.setBriefCommunication(briefCommunicationName);
        projectDto.setBriefCDG(briefCDGName);
        projectDto.setRegulatoryBrief(regulatoryBriefName);

        // Upload attached documents
        Set<String> attachedDocumentsNames = new HashSet<>();
        for (MultipartFile file : attachedDocuments) {
            DocumentDto attachedDocumentDto = projectService.uploadFile(file);
            attachedDocumentsNames.add(attachedDocumentDto.getDocumentLabel());
        }

        // Log attached documents
        logger.info("Uploaded attached documents: {}", attachedDocumentsNames);

        // Set attached documents in projectDto
        projectDto.setAttachedDocuments(attachedDocumentsNames);

        // Log final projectDto before saving
        logger.info("Final ProjectDto: {}", projectDto);

        // Save the project and return the response
        ProjectDto savedProject = projectService.addProject(projectDto);
        logger.info("Project saved successfully with ID: {}", savedProject.getId());

        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }





    @GetMapping("/all")
    @PreAuthorize(value = "hasAnyAuthority('OWNER', 'SPOC')")
    public ResponseEntity<Set<ProjectDto>> getAllProjects(){
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable("id") Long id){
        return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
    }

    /*
    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable("id") Long id, @RequestBody ProjectDto updatedProjectDto){
        return new ResponseEntity<>(projectService.updateProject(id, updatedProjectDto), HttpStatus.OK);
    }

     */


    @PutMapping("/update/{id}")
    @PreAuthorize(value = "hasAnyAuthority('OWNER')")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable("id") Long id,
            MultipartHttpServletRequest request) throws JsonProcessingException {

        MultipartFile expressionOfNeed = request.getFile("expressionOfNeed");
        MultipartFile briefCommunication = request.getFile("briefCommunication");
        MultipartFile briefCDG = request.getFile("briefCDG");
        MultipartFile regulatoryBrief = request.getFile("regulatoryBrief");
        MultipartFile[] attachedDocuments = request.getFiles("attachedDocuments[]").toArray(new MultipartFile[0]);

        String projectDtoParam = request.getParameter("projectDto");
        if (projectDtoParam == null || projectDtoParam.isEmpty()) {
            throw new IllegalArgumentException("ProjectDto parameter is missing or empty");
        }

        ProjectDto projectDto = new ObjectMapper().readValue(projectDtoParam, ProjectDto.class);

        System.out.println("expressionOfNeed: " + (expressionOfNeed != null ? expressionOfNeed.getOriginalFilename() : "null"));
        System.out.println("briefCommunication: " + (briefCommunication != null ? briefCommunication.getOriginalFilename() : "null"));
        System.out.println("briefCDG: " + (briefCDG != null ? briefCDG.getOriginalFilename() : "null"));
        System.out.println("regulatoryBrief: " + (regulatoryBrief != null ? regulatoryBrief.getOriginalFilename() : "null"));

        DocumentDto documentExpressionOfNeedName = projectService.uploadFile(expressionOfNeed);
        DocumentDto documentBriefCommunicationName = projectService.uploadFile(briefCommunication);
        DocumentDto documentBriefCDGName = projectService.uploadFile(briefCDG);
        DocumentDto documentRegulatoryBriefName = projectService.uploadFile(regulatoryBrief);

        String expressionOfNeedName = documentExpressionOfNeedName.getDocumentLabel();
        String briefCommunicationName = documentBriefCommunicationName.getDocumentLabel();
        String briefCDGName = documentBriefCDGName.getDocumentLabel();
        String regulatoryBriefName = documentRegulatoryBriefName.getDocumentLabel();

        projectDto.setExpressionOfNeed(expressionOfNeedName);
        projectDto.setBriefCommunication(briefCommunicationName);
        projectDto.setBriefCDG(briefCDGName);
        projectDto.setRegulatoryBrief(regulatoryBriefName);

        Set<String> attachedDocumentsNames = new HashSet<>();
        for (MultipartFile file : attachedDocuments) {
            DocumentDto attachedDocumentDto = projectService.uploadFile(file);
            attachedDocumentsNames.add(attachedDocumentDto.getDocumentLabel());
        }

        projectDto.setAttachedDocuments(attachedDocumentsNames);

        ProjectDto updatedProject = projectService.updateProject(id, projectDto);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*
    @PutMapping("/next-gate/{projectId}")
    @PreAuthorize(value = "hasAnyAuthority('OWNER')")
    public ResponseEntity<String> goToNextGate(@PathVariable Long projectId, JwtAuthenticationToken jwt) {
        String ownerEmail = jwt.getToken().getClaim("email");
        try {
            projectService.goToNextGateProject(projectId, ownerEmail);
            return ResponseEntity.ok("Successfully moved to the next gate for project ID: " + projectId);
        }
        catch (UnauthorizedPassageToNextGateException | ReachedFinalGateException e1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e1.getMessage());
        }
    }

     */


    @PutMapping("/next-gate/{projectId}")
    @PreAuthorize(value = "hasAnyAuthority('OWNER')")
    public ResponseEntity<String> goToNextGate(@PathVariable Long projectId, JwtAuthenticationToken jwt) {
        String ownerEmail = jwt.getToken().getClaim("email");
        projectService.goToNextGateProject(projectId, ownerEmail);
        return ResponseEntity.ok("Successfully moved to the next gate for project ID: " + projectId);
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////





    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable("id") Long id){
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/api/internal/users/by-departments")
    public List<UserDto> getUserByDepartment(@RequestBody List<String> departments) {
        return projectService.getUserByDepartment(departments);
    }


    @PostMapping("/affect-signal/{projectId}/{username}")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<String> affectProjectToInterlocutorSignalingImpact(@PathVariable("projectId") Long id, @PathVariable("username") String username,
                                                                                 JwtAuthenticationToken jwt){
        String spoc = jwt.getToken().getClaim("email");
        return new ResponseEntity<>(projectService.affectProjectToInterlocutorSignalingImpact(spoc, username, id), HttpStatus.OK);

    }


    @PostMapping("/affect-respond/{username}/{projectId}")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<String> affectProjectToInterlocutorRespondingImpact(@PathVariable("username") String username, @PathVariable("projectId") Long id,
                                                                             JwtAuthenticationToken jwt){
        String spoc = jwt.getToken().getClaim("email");
        return new ResponseEntity<>(projectService.affectProjectToInterlocutorRespondingImpact(spoc, username, id), HttpStatus.OK);

    }


    @GetMapping("/ownerprojects/{ownerUsername}")
    @PreAuthorize(value = "hasAuthority('OWNER')")
    public ResponseEntity<Set<ProjectDto>> getProjectsByOwnerUsername(@PathVariable("ownerUsername") String ownerUsername){
        return new ResponseEntity<>(projectService.getProjectsByOwnerUsername(ownerUsername), HttpStatus.OK);
    }



    @GetMapping("/filter")
    public ResponseEntity<Set<ProjectDto>> getFilteredProjects(
            @RequestParam(value = "projectType", required = false) String projectType,
            @RequestParam(value = "marketType", required = false) String marketType)
    {

        Set<ProjectDto> projects = projectService.filterProjects(projectType, marketType);

        return ResponseEntity.ok(projects);
    }



    @GetMapping("/filter-spoc")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<ProjectFrontDto>> getFilteredProjects(
            @RequestParam("department") String department,
            @RequestParam(value = "projectType", required = false) String projectType,
            @RequestParam(value = "marketType", required = false) String marketType)
    {

        Set<ProjectFrontDto> projects = projectService.filterProjectsForSpoc(department, projectType, marketType);

        return ResponseEntity.ok(projects);
    }





    @GetMapping("/not-affected-projects")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<ProjectDto>> getProjectsNotAffected(@RequestParam("department") String department){
        Set<ProjectDto> projectDtos = projectService.getProjectsNotAffected(department);
        if (projectDtos.isEmpty()){
            throw new ProjectNotFoundException("No project was found !");
        }
        return new ResponseEntity<>(projectDtos, HttpStatus.OK);
    }


    @GetMapping("/interlocutors-impact")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<UserDto>> getInterlocutorsSignalingImpactByDepartment(@RequestParam("department") String department){
        Set<UserDto> users = projectService.getInterlocutorsSignalingImpactByDepartment(department);
        if (users.isEmpty()){
            throw new UserNotFoundException("No user was found !");
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/interlocutors-response")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<UserDto>> getInterlocutorsRespondingImpactByDepartment(@RequestParam("department") String department){
        Set<UserDto> users = projectService.getInterlocutorsRespondingImpactByDepartment(department);
        if (users.isEmpty()){
            throw new UserNotFoundException("No user was found !");
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/nextgate/{projectId}")
    public ResponseEntity<GateType> getNextGateForProject(@PathVariable("projectId") Long projectId) {
        Gate nextGate = projectService.getNextGateProject(projectId);
        //return new ResponseEntity<>(nextGate.getGateType(), HttpStatus.OK);
        return ResponseEntity.ok(nextGate.getGateType());
    }



    @GetMapping("/spoc-projects/{department}")
    @PreAuthorize("hasAuthority('SPOC')")
    public ResponseEntity<Set<ProjectFrontDto>> getProjectsForSpoc(@PathVariable String department) {
        Set<ProjectFrontDto> projectFrontDtos = projectService.getProjectsForSpoc(department);

        if (projectFrontDtos == null || projectFrontDtos.isEmpty()) {
            throw new ProjectNotFoundException("No project was found!");
        }

        // Ensure uniqueness (if necessary)
        Set<ProjectFrontDto> distinctProjects = new HashSet<>(projectFrontDtos);

        return ResponseEntity.ok(distinctProjects);
    }

    @GetMapping("/delete-project/{id}")
    public String physicalDeleteProject(@PathVariable("id") Long id){
        projectRepository.deleteById(id);
        logger.info("product has been deleted..", id);

        return "product has been deleted.. " + id;
    }




    @GetMapping("/spoc-projects/export/{department}")
    @PreAuthorize("hasAuthority('SPOC')")
    public void exportSpocProjectsToCSV(@PathVariable String department, HttpServletResponse response) throws IOException {
        Set<ProjectFrontDto> projects = projectService.getProjectsForSpoc(department);

        if (projects == null || projects.isEmpty()) {
            throw new ProjectNotFoundException("No project was found!");
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=spoc-projects.csv");

        PrintWriter writer = response.getWriter();

        // CSV Header (you can add more fields if needed)
        writer.println("ID,Owner Username,Owner Full Name,Title,Description,Market Type,Project Type,Assigned Status,Departments,Date Start TTM,Date Created");

        for (ProjectFrontDto project : projects) {
            writer.println(String.format(
                    "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    project.getId(),
                    escape(project.getOwnerUsername()),
                    escape(project.getOwnerFullName()),
                    escape(project.getTitle()),
                    escape(project.getDescription()),
                    escape(project.getMarketType()),
                    escape(project.getProjectType()),
                    project.getAssignedToInterlocutors() != null ? project.getAssignedToInterlocutors().name() : "",
                    escape(project.getDepartments() != null ? String.join(";", project.getDepartments()) : ""),
                    project.getDateStartTtm() != null ? project.getDateStartTtm().toString() : "",
                    project.getDateCreation() != null ? project.getDateCreation().toString() : ""
            ));
        }

        writer.flush();
        writer.close();
    }


    // ✅ Add this utility method here ↓↓↓
    private String escape(String input) {
        if (input == null) return "";
        boolean hasSpecialChar = input.contains(",") || input.contains("\"") || input.contains("\n");
        String escaped = input.replace("\"", "\"\"");
        return hasSpecialChar ? "\"" + escaped + "\"" : escaped;
    }


}
