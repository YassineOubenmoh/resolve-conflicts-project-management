package ma.inwi.msproject.controller;

import lombok.extern.slf4j.Slf4j;
import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.service.GateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/gate")
public class GateController {

    private static final Logger logger = LoggerFactory.getLogger(GateController.class);

    @Autowired
    private final GateService gateService;

    public GateController(GateService gateService) {
        this.gateService = gateService;
    }


    @PostMapping("/add")
    public ResponseEntity<GateDto> addGate(@RequestBody GateDto gateDto){
        return new ResponseEntity<>(gateService.addGate(gateDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<GateDto>> getAllGates(){
        return new ResponseEntity<>(gateService.getAllGates(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<GateDto> getGateById(@PathVariable("id") Long id){
        return new ResponseEntity<>(gateService.getGateById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GateDto> updateGate(@PathVariable("id") Long id, @RequestBody GateDto updatedGateDto) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        logger.info("Updating Gate with ID: {}", id);
        return new ResponseEntity<>(gateService.updateGate(id, updatedGateDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGate(@PathVariable("id") Long id){
        gateService.deleteGate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

