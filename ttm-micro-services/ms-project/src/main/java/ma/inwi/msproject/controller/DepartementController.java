package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/departement")
public class DepartementController {

    @Autowired
    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }


    @PostMapping("/add")
    public ResponseEntity<DepartementDto> addDepartement(@RequestBody DepartementDto departementDto){
        return new ResponseEntity<>(departementService.addDepartement(departementDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<DepartementDto>> getAllDepartements(){
        return new ResponseEntity<>(departementService.getAllDepartements(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<DepartementDto> getDepartementById(@PathVariable("id") Long id){
        return new ResponseEntity<>(departementService.getDepartementById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DepartementDto> updateDepartement(@PathVariable("id") Long id, @RequestBody DepartementDto updatedDepartementDto){
        return new ResponseEntity<>(departementService.updateDepartement(id, updatedDepartementDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartement(@PathVariable("id") Long id){
        departementService.deleteDepartement(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
