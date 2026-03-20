package ma.inwi.msproject.controller;

import lombok.RequiredArgsConstructor;
import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.service.TrackingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/add")
    //@PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<TrackingDto> addGate(@RequestBody TrackingDto trackingDto){
        return new ResponseEntity<>(trackingService.addTracking(trackingDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<TrackingDto>> getAllTrackings(){
        return new ResponseEntity<>(trackingService.getAllTrackings(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<TrackingDto> getTrackingById(@PathVariable("id") Long id){
        return new ResponseEntity<>(trackingService.getTrackingById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TrackingDto> updateTracking(@PathVariable("id") Long id, @RequestBody TrackingDto trackingDto){
        return new ResponseEntity<>(trackingService.updateTracking(id, trackingDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTracking(@PathVariable("id") Long id){
        trackingService.deleteTracking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

