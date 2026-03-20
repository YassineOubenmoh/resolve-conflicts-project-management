package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.DepartementGateProjectDto;
import ma.inwi.msproject.dto.frontdtos.GateProjectFront;
import ma.inwi.msproject.service.DepartementGateProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/departement-gateproject")
public class DepartementGateProjectController {

    @Autowired
    private final DepartementGateProjectService departementGateProjectService;

    public DepartementGateProjectController(DepartementGateProjectService departementGateProjectService) {
        this.departementGateProjectService = departementGateProjectService;
    }

    @PostMapping("/{gateProjectId}/{departementId}")
    public ResponseEntity<DepartementGateProjectDto> affectGateProjectToDepartement(@PathVariable("gateProjectId") Long gateProjectId, @PathVariable("departementId") Long departementId, JwtAuthenticationToken jwt){
        String emailSender = jwt.getToken().getClaim("email");

        return new ResponseEntity<>(departementGateProjectService.affectGateProjectToDepartement(gateProjectId, departementId, emailSender), HttpStatus.CREATED);
    }

    @PostMapping("/{gateProjectId}")
    public ResponseEntity<List<DepartementGateProjectDto>> affectGateProjectToListDepartements(@PathVariable("gateProjectId") Long gateProjectId, @RequestBody List<String> departementLabels, JwtAuthenticationToken jwt){
        String emailSender = jwt.getToken().getClaim("email");

        return new ResponseEntity<>(departementGateProjectService.affectGateProjectToListDepartements(gateProjectId, departementLabels, emailSender), HttpStatus.CREATED);

    }

    @GetMapping("/all")
    public ResponseEntity<Set<DepartementGateProjectDto>> getAllProjectGatesAffectationsToDepartement(){
        return new ResponseEntity<>(departementGateProjectService.getAllProjectGatesAffectationsToDepartement(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<DepartementGateProjectDto> getDepartementGateProjectAffectationById(@PathVariable("id") Long id){
        return new ResponseEntity<>(departementGateProjectService.getDepartementGateProjectAffectationById(id), HttpStatus.OK);
    }

    @PutMapping("/update-gate/{id}/{gateProjectId}")
    public ResponseEntity<DepartementGateProjectDto> modifyAffectedGateToDepartement(@PathVariable("id") Long id, @PathVariable("gateProjectId") Long gateProjectId){
        return new ResponseEntity<>(departementGateProjectService.modifyAffectedGateToDepartement(id, gateProjectId), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DepartementGateProjectDto> updateGateProjectAffectationToDepartement(@PathVariable("id") Long id, @RequestBody DepartementGateProjectDto updatedDepartementGateProjectDto){
        return new ResponseEntity<>(departementGateProjectService.updateGateProjectAffectationToDepartement(id, updatedDepartementGateProjectDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGateProjectAffectationToDepartement(@PathVariable("id") Long id){
        departementGateProjectService.deleteGateProjectAffectationToDepartement(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/gates-affected-project/{department}/{projectId}")
    public ResponseEntity<Set<GateProjectFront>> getGateProjectsByDepartmentAndProjectId(
            @PathVariable("department") String department,
            @PathVariable("projectId") Long projectId
    ){
        return new ResponseEntity<>(departementGateProjectService.getGateProjectsByDepartmentAndProjectId(projectId, department), HttpStatus.OK);
    }


    @GetMapping("/gates-affected-department/{department}")
    public ResponseEntity<Set<GateProjectFront>> getGateProjectsByDepartment(
            @PathVariable("department") String department, JwtAuthenticationToken jwt
    ){
        String username = jwt.getToken().getClaim("preferred_username");
        return new ResponseEntity<>(departementGateProjectService.getGateProjectsByDepartment(department, username), HttpStatus.OK);
    }


    @GetMapping("/gates-affected-spoc/{department}")
    @PreAuthorize(value = "hasAuthority('SPOC')")
    public ResponseEntity<Set<GateProjectFront>> getGateProjectsByDepartmentSpoc(
            @PathVariable("department") String department
    ){
        return new ResponseEntity<>(departementGateProjectService.getGateProjectsSpocByDepartment(department), HttpStatus.OK);
    }


    @PostMapping("/affect-set-gates/{departmentId}")
    public ResponseEntity<Set<DepartementGateProjectDto>> affectGateProjectsToDepartments(@PathVariable("departmentId") Long departmentId, @RequestBody Set<Long> gateProjects, JwtAuthenticationToken jwt){
        String emailSender = jwt.getToken().getClaim("email");

        return ResponseEntity.ok(departementGateProjectService.affectGateProjectsToDepartment(gateProjects, departmentId, emailSender));

    }


}
