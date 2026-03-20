package ma.inwi.msproject.controller;

import lombok.RequiredArgsConstructor;
import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.service.TrackingGateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracking-gate")
public class TrackingGateController {

    private final TrackingGateService trackingGateService;

    @PostMapping("/{trackingId}/{gateId}")
    public ResponseEntity<TrackingGateDto> affectGateToTracking(@PathVariable("gateId") Long gateId, @PathVariable("trackingId") Long trackingId){
        return new ResponseEntity<>(trackingGateService.affectGateToTracking(gateId, trackingId), HttpStatus.CREATED);
    }

    /*
    @GetMapping("/order-gates/{trackingId}")
    public ResponseEntity<Set<TrackingGateDto>> fillGateOrder(@PathVariable("trackingId") Long trackingId){
        return new ResponseEntity<>(trackingGateService.getRelatedGatesToTracking(trackingId), HttpStatus.OK);
    }

     */

    @GetMapping("/find/{trackingId}")
    public ResponseEntity<Set<TrackingGateDto>> getRelatedGatesToTracking(@PathVariable("trackingId") Long id){
        return new ResponseEntity<>(trackingGateService.getRelatedGatesToTracking(id), HttpStatus.OK);
    }

}

