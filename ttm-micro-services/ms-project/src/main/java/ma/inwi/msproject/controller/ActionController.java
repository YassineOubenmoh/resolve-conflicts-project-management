package ma.inwi.msproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.client.ProjectRestClient;
import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.dto.DocumentDto;
import ma.inwi.msproject.dto.DocumentResponseFrontDto;
import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.entities.RequiredAction;
import ma.inwi.msproject.exceptions.RequiredActionNotFoundException;
import ma.inwi.msproject.repositories.ActionRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import ma.inwi.msproject.service.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/action")
public class ActionController {

    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    private final ActionService actionService;
    private final ProjectRestClient projectRestClient;
    private final RequiredActionRepository requiredActionRepository;
    private ActionRepository actionRepository;

    public ActionController(ActionService actionService, ProjectRestClient projectRestClient, RequiredActionRepository requiredActionRepository, ActionRepository actionRepository) {
        this.actionService = actionService;
        this.projectRestClient = projectRestClient;
        this.requiredActionRepository = requiredActionRepository;
        this.actionRepository = actionRepository;
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName) {
        try {
            ResponseEntity<byte[]> fileResponse = projectRestClient.downloadFile(fileName);

            if (fileResponse.getBody() != null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(fileResponse.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading file: " + e.getMessage()).getBytes());
        }
    }


    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('INTERLOCUTEUR_SIGNALE_IMPACT')")
    public ResponseEntity<ActionDto> addAction(MultipartHttpServletRequest request, JwtAuthenticationToken jwt) throws JsonProcessingException {
        // 1. Retrieve file and parameters
        MultipartFile actionDocument = request.getFile("actionDocument");
        String actionDtoParam = request.getParameter("actionDto");

        if (actionDtoParam == null || actionDtoParam.isEmpty()) {
            throw new IllegalArgumentException("ActionDto parameter is missing or empty");
        }

        // 2. Extract user information from JWT
        String createdBy = jwt.getToken().getClaim("preferred_username");
        String emailSender = jwt.getToken().getClaim("email");
        logger.info("User creating action: {}, Email: {}", createdBy, emailSender);

        // 3. Parse actionDto
        ObjectMapper objectMapper = new ObjectMapper();
        ActionDto actionDto = objectMapper.readValue(actionDtoParam, ActionDto.class);
        actionDto.setActionCreatedBy(createdBy);
        actionDto.setImpactSenderEmail(emailSender);

        // Log requiredActionId
        logger.info("Received RequiredAction ID: {}", actionDto.getRequiredActionId());

        // 4. Validate required action
        RequiredAction requiredAction = requiredActionRepository.findById(actionDto.getRequiredActionId())
                .orElseThrow(() -> new RequiredActionNotFoundException(
                        "Required action with ID " + actionDto.getRequiredActionId() + " was not found!"
                ));

        // 5. Extract related entities
        Departement departement = requiredAction.getDepartementGateProject().getDepartement();
        Gate gate = requiredAction.getDepartementGateProject().getGateProject().getTrackingGate().getGate();
        Project project = requiredAction.getDepartementGateProject().getGateProject().getProject();
        Long projectId = project.getId();
        logger.info("Extracted Project ID: {}", projectId);

        // 6. Upload initial document
        DocumentDto uploadedDocument = actionService.uploadFile(actionDocument);
        logger.info("Uploaded document with ID: {}", uploadedDocument.getId());

        // 7. Prepare updated document metadata
        DocumentDto updatedDocument = DocumentDto.builder()
                .actionLabel(actionDto.getActionLabel())
                .projectId(projectId)
                .requiredActionId(actionDto.getRequiredActionId())
                .documentLabel(uploadedDocument.getDocumentLabel())
                .typeDocument(uploadedDocument.getTypeDocument())
                .dateUpload(uploadedDocument.getDateUpload())
                .size(uploadedDocument.getSize())
                .department(departement.getDepartement())
                .authorName(createdBy)
                .gateLabel(gate.getGateType())
                .build();

        logger.info("Prepared document update with Project ID: {}", updatedDocument.getProjectId());

        // 8. Apply document update
        DocumentDto finalDocument = actionService.updateDocument(uploadedDocument.getId(), updatedDocument);
        logger.info("Final document project ID after update: {}", finalDocument.getProjectId());

        // 9. Link updated document to action
        actionDto.setActionDocument(finalDocument.getDocumentLabel());

        // 10. Persist action and return
        ActionDto savedAction = actionService.addAction(actionDto);
        logger.info("Action saved successfully with ID: {}", savedAction.getId());

        return new ResponseEntity<>(savedAction, HttpStatus.CREATED);
    }





    @PutMapping("/respond/{id}")
    @PreAuthorize("hasAuthority('INTERLOCUTEUR_RETOUR_IMPACT')")
    public ResponseEntity<ActionDto> respondOnImpact(
            @PathVariable("id") Long id,
            MultipartHttpServletRequest request,
            JwtAuthenticationToken jwt
    ) throws JsonProcessingException {

        // 1. Retrieve the uploaded response document
        MultipartFile responseDocument = request.getFile("responseDocument");

        // 2. Extract user information from JWT
        String validatedBy = jwt.getToken().getClaim("preferred_username");
        String responseEmailSender = jwt.getToken().getClaim("email");

        // 3. Get and validate the ActionDto JSON parameter
        String actionDtoParam = request.getParameter("actionDto");
        if (actionDtoParam == null || actionDtoParam.isEmpty()) {
            throw new IllegalArgumentException("ActionDto parameter is missing or empty");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ActionDto actionDto = objectMapper.readValue(actionDtoParam, ActionDto.class);

        actionDto.setValidatedBy(validatedBy);
        actionDto.setResponseEmailSender(responseEmailSender);

        // 4. Fetch requiredAction and related department/gate
        RequiredAction requiredAction = requiredActionRepository.findById(actionDto.getRequiredActionId())
                .orElseThrow(() -> new RequiredActionNotFoundException(
                        "Required action with ID " + actionDto.getRequiredActionId() + " was not found!"
                ));

        Departement departement = requiredAction.getDepartementGateProject().getDepartement();
        Gate gate = requiredAction.getDepartementGateProject().getGateProject().getTrackingGate().getGate();
        Project project = requiredAction.getDepartementGateProject().getGateProject().getProject();

        // 5. Upload and update response document metadata
        DocumentDto uploadedDocument = actionService.uploadFile(responseDocument);

        DocumentDto updatedDocument = DocumentDto.builder()
                .actionLabel(actionDto.getResponseToActionLabel())
                .projectId(project.getId())
                .requiredActionId(requiredAction.getId())
                .documentLabel(uploadedDocument.getDocumentLabel())
                .typeDocument(uploadedDocument.getTypeDocument())
                .dateUpload(uploadedDocument.getDateUpload())
                .size(uploadedDocument.getSize())
                .department(departement.getDepartement())
                .authorName(validatedBy)
                .gateLabel(gate.getGateType())
                .build();

        DocumentDto finalDocument = actionService.updateDocument(uploadedDocument.getId(), updatedDocument);

        // 6. Link the updated response document to the action
        actionDto.setResponseDocument(finalDocument.getDocumentLabel());

        // 7. Validate and return updated action
        ActionDto validatedAction = actionService.validateImpact(id, actionDto);
        return new ResponseEntity<>(validatedAction, HttpStatus.CREATED);
    }


    /*
    @PostMapping("/add")
    public ResponseEntity<ActionDto> addAction(@RequestBody ActionDto actionDto){
        return new ResponseEntity<>(actionService.addAction(actionDto), HttpStatus.CREATED);
    }
     */

    @GetMapping("/all")
   /* @PreAuthorize("hasAuthority('INTERLOCUTEUR_RETOUR_IMPACT') or " +
            "hasAuthority('INTERLOCUTEUR_SIGNALE_IMPACT')") */
    public ResponseEntity<Set<ActionDto>> getAllActions(){
        return new ResponseEntity<>(actionService.getAllActions(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'INTERLOCUTEUR_RETOUR_IMPACT', 'SPOC')")
    public ResponseEntity<ActionDto> getActionById(@PathVariable("id") Long id){
        return new ResponseEntity<>(actionService.getActionById(id), HttpStatus.OK);
    }

    /*
    @PutMapping("/update/{id}")
    public ResponseEntity<ActionDto> updateAction(@PathVariable("id") Long id, @RequestBody ActionDto updatedActionDto){
        return new ResponseEntity<>(actionService.updateAction(id, updatedActionDto), HttpStatus.OK);
    }

     */

    /*
    @PutMapping("/update/{id}")
    public ResponseEntity<ActionDto> updateAction(
            @PathVariable("id") Long id,
            MultipartHttpServletRequest request) throws JsonProcessingException {

        MultipartFile actionDocument = request.getFile("actionDocument");
        MultipartFile responseDocument = request.getFile("responseDocument");

        String actionDtoParam = request.getParameter("actionDto");
        if (actionDtoParam == null || actionDtoParam.isEmpty()) {
            throw new IllegalArgumentException("ActionDto parameter is missing or empty");
        }

        ActionDto updatedActionDto = new ObjectMapper().readValue(actionDtoParam, ActionDto.class);

        String actionDocumentName = actionService.uploadFile(actionDocument);
        String responseDocumentName = actionService.uploadFile(responseDocument);

        updatedActionDto.setActionDocument(actionDocumentName);
        updatedActionDto.setResponseDocument(responseDocumentName);

        return new ResponseEntity<>(actionService.updateAction(id, updatedActionDto), HttpStatus.OK);
    }

     */

    @PutMapping("/update-impact/{id}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'SPOC')")
    public ResponseEntity<ActionDto> updateImpact(@PathVariable("id") Long id, MultipartHttpServletRequest request, JwtAuthenticationToken jwt) throws JsonProcessingException {

        MultipartFile actionDocument = request.getFile("actionDocument");

        String createdBy = jwt.getToken().getClaim("preferred_username");

        String actionDtoParam = request.getParameter("actionDto");
        if (actionDtoParam == null || actionDtoParam.isEmpty()) {
            throw new IllegalArgumentException("ActionDto parameter is missing or empty");
        }

        ActionDto actionDto = new ObjectMapper().readValue(actionDtoParam, ActionDto.class);

        RequiredAction requiredAction = requiredActionRepository.findById(actionDto.getRequiredActionId()).orElseThrow(
                () -> new RequiredActionNotFoundException("Required action with " + actionDto.getRequiredActionId() + " was not found !"));
        Departement departement = requiredAction.getDepartementGateProject().getDepartement();
        Gate gate = requiredAction.getDepartementGateProject().getGateProject().getTrackingGate().getGate();

        DocumentDto actionDocumentObj = actionService.uploadFile(actionDocument);

        DocumentDto updatedResponseDocument = DocumentDto.builder()
                .actionLabel(actionDto.getActionLabel())
                .documentLabel(actionDocumentObj.getDocumentLabel())
                .typeDocument(actionDocumentObj.getTypeDocument())
                .dateUpload(actionDocumentObj.getDateUpload())
                .size(actionDocumentObj.getSize())
                .department(departement.getDepartement())
                .authorName(createdBy)
                .gateLabel(gate.getGateType())
                .build();

        DocumentDto updateResponse = actionService.updateDocument(actionDocumentObj.getId(), updatedResponseDocument);

        actionDto.setActionDocument(actionDocumentObj.getDocumentLabel());

        return new ResponseEntity<>(actionService.updateImpact(id, actionDto), HttpStatus.CREATED);
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAction(@PathVariable("id") Long id){
        actionService. deleteAction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/alldata")
    public ResponseEntity<Set<DocumentResponseFrontDto>> getAllDocumentsData() {
        Set<DocumentResponseFrontDto> documentResponseFrontDtos = new HashSet<>();
        Set<ActionDto> actions = actionService.getAllActions();

        for (ActionDto actionDto : actions) {
            Long actionId = actionDto.getId();
            try {
                DocumentResponseFrontDto response = actionService.getDocumentActionResponse(actionId);
                if (response != null) {
                    documentResponseFrontDtos.add(response);
                }
            } catch (Exception e) {
                System.err.println("Error fetching response document for action ID " + actionId + ": " + e.getMessage());
            }

            try {
                DocumentResponseFrontDto action = actionService.getDocumentAction(actionId);
                if (action != null) {
                    documentResponseFrontDtos.add(action);
                }
            } catch (Exception e) {
                System.err.println("Error fetching action document for action ID " + actionId + ": " + e.getMessage());
            }
        }

        return new ResponseEntity<>(documentResponseFrontDtos, HttpStatus.OK);
    }


    @GetMapping("/interlocutor-actions")
    //@PreAuthorize(value = "hasAuthority('INTERLOCUTEUR_SIGNALE_IMPACT')")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'SPOC')")
    public ResponseEntity<Set<ActionDto>> getImpactAndReturnsByInterlocutor(JwtAuthenticationToken jwt){
        String interlocutorImpactUsername = jwt.getToken().getClaim("preferred_username");
        return new ResponseEntity<>(actionService.getImpactAndReturnsByInterlocutor(interlocutorImpactUsername), HttpStatus.OK);
    }

    @GetMapping("/actions-by-required/{requiredActionId}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'INTERLOCUTEUR_RETOUR_IMPACT')")
    public ResponseEntity<Set<ActionDto>> getImpactAndReturnsByInterlocutor(@PathVariable("requiredActionId") Long requiredActionId){
        return new ResponseEntity<>(actionService.getActionsByRequiredAction(requiredActionId), HttpStatus.OK);
    }






}
