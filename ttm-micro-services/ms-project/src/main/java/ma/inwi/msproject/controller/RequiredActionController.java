package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.RequiredActionDto;
import ma.inwi.msproject.dto.RequiredActionGlobalResponse;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.service.RequiredActionService;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/required-action")
public class RequiredActionController {

    @Autowired
    private final RequiredActionService requiredActionService;

    public RequiredActionController(RequiredActionService requiredActionService) {
        this.requiredActionService = requiredActionService;
    }

    /*
    @PostMapping("/add")
    public ResponseEntity<RequiredActionDto> addActionRequise(@RequestBody RequiredActionDto requiredActionDto){
        return new ResponseEntity<>(requiredActionService.addActionRequise(requiredActionDto), HttpStatus.CREATED);
    }

     */

    @GetMapping("/all")
    //@PreAuthorize(value = "hasAnyAuthority('SPOC')")
    public ResponseEntity<Set<RequiredActionDto>> getAllActionRequises(){
        return new ResponseEntity<>(requiredActionService.getAllActionRequises(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'INTERLOCUTEUR_RETOUR_IMPACT', 'SPOC')")
    public ResponseEntity<RequiredActionDto> getActionRequiseById(@PathVariable("id") Long id){
        return new ResponseEntity<>(requiredActionService.getActionRequiseById(id), HttpStatus.OK);
    }

    /*
    @PutMapping("/update/{id}")
    public ResponseEntity<RequiredActionDto> updateActionRequise(@PathVariable("id") Long id, @RequestBody RequiredActionDto updatedRequiredActionDto){
        return new ResponseEntity<>(requiredActionService.updateActionRequise(id, updatedRequiredActionDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize(value = "hasAnyAuthority('OWNER')")
    public ResponseEntity<?> deleteActionRequise(@PathVariable("id") Long id){
        requiredActionService.deleteActionRequise(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

     */

    @GetMapping("/global-response/{requiredActionId}")
    public ResponseEntity<RequiredActionGlobalResponse> getRequiredActionGlobalInfoById(@PathVariable("requiredActionId") Long requiredActionId){
        return new ResponseEntity<>(requiredActionService.getRequiredActionGlobalInfoById(requiredActionId), HttpStatus.OK);
    }


    @GetMapping("/project-required-actions/{projectId}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'INTERLOCUTEUR_RETOUR_IMPACT', 'SPOC')")
    public ResponseEntity<Set<RequiredActionDto>> getRequiredActionsByProjectId(@PathVariable("projectId") Long projectId){
        return new ResponseEntity<>(requiredActionService.getRequiredActionsByProjectId(projectId), HttpStatus.OK);
    }



    @GetMapping("/gate-project-required-actions/{projectId}/{gate}")
    @PreAuthorize(value = "hasAnyAuthority('INTERLOCUTEUR_SIGNALE_IMPACT', 'INTERLOCUTEUR_RETOUR_IMPACT', 'SPOC')")
    public ResponseEntity<Set<RequiredActionDto>> getRequiredActionsByProjectIdAndGate(
            @PathVariable("projectId") Long projectId,
            @PathVariable("gate") GateType gate
            ){
        return new ResponseEntity<>(requiredActionService.getRequiredActionsByProjectIdAndGate(projectId, gate), HttpStatus.OK);
    }

    @GetMapping("/required-action-by-label")
    public ResponseEntity<RequiredActionDto> getRequiredActionIdFromLabel(
            @RequestParam("requiredActionLabel") String requiredActionLabel,
            @RequestParam("projectId") Long projectId) {
        return new ResponseEntity<>(requiredActionService.getRequiredActionIdFromLabel(requiredActionLabel, projectId), HttpStatus.OK);
    }




}
