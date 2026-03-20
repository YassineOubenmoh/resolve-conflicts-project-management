package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.dto.GateProjectResponseDto;
import ma.inwi.msproject.service.GateProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/gate-project")
public class GateProjectController {

    @Autowired
    private final GateProjectService gateProjectService;

    public GateProjectController(GateProjectService gateProjectService) {
        this.gateProjectService = gateProjectService;
    }

    @PostMapping("/add")
    public ResponseEntity<GateProjectDto> addGateProject(@RequestBody GateProjectDto gateProjectDto){
        return new ResponseEntity<>(gateProjectService.addGateProject(gateProjectDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<GateProjectDto>> getAllGateProjects(){
        return new ResponseEntity<>(gateProjectService.getAllGateProjects(), HttpStatus.OK);
    }

    @GetMapping("/project-gates-responses/{projectId}")
    public ResponseEntity<Set<GateProjectResponseDto>> getGatesResponseProjectByProjectId(@PathVariable("projectId") Long projectId){
        return new ResponseEntity<>(gateProjectService.getGatesResponseProjectByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping("/project-gates/{projectId}")
    public ResponseEntity<Set<GateProjectDto>> getGatesProjectByProjectId(@PathVariable("projectId") Long projectId){
        return new ResponseEntity<>(gateProjectService.getGatesProjectByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<GateProjectDto> getGateProjectById(@PathVariable("id") Long id){
        return new ResponseEntity<>(gateProjectService.getGateProjectById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GateProjectDto> updateGateProject(@PathVariable("id") Long id, @RequestBody GateProjectDto updatedGateProjectDto){
        //return new ResponseEntity<>(gateProjectService.updateGateProject(id, updatedGateProjectDto), HttpStatus.OK);
        GateProjectDto gateProjectDto = gateProjectService.updateGateProject(id, updatedGateProjectDto);
        return ResponseEntity.ok(gateProjectDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGateProject(@PathVariable("id") Long id){
        gateProjectService.deleteGateProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/suspend/{id}")
    @PreAuthorize(value = "hasAuthority('OWNER')")
    public ResponseEntity<GateProjectDto> suspendGateProgress(@PathVariable("id") Long gateProjectId, JwtAuthenticationToken jwt){
        String emailOwner = jwt.getToken().getClaim("email");
        return new ResponseEntity<>(gateProjectService.changeGateProgress(gateProjectId, emailOwner), HttpStatus.OK);
    }


    @GetMapping("/current-gate/{projectId}")
    public Long getCurrentGateIdByProjectId(@PathVariable("projectId") Long projectId) {
        return gateProjectService.getCurrentGateIdByProjectId(projectId);
    }
}

