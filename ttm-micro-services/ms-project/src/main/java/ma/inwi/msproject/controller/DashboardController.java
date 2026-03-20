package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.dashboard.*;
import ma.inwi.msproject.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/dashboard/")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping("/pie-tracking")
    public ResponseEntity<Set<PieTrackingDto>> getTrackingPercentages(){
        return new ResponseEntity<>(dashboardService.getTrackingPercentages(), HttpStatus.OK);
    }


    /*
    @GetMapping("/pie-tracking/{dateFrom}/{dateTo}")
    public ResponseEntity<Set<PieTrackingDto>> getTrackingPercentagesBetweenDates(
            @PathVariable("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateTo) {

        Set<PieTrackingDto> result = dashboardService.getTrackingPercentagesBetweenDates(dateFrom, dateTo);
        return ResponseEntity.ok(result);
    }


     */


    @GetMapping("/pie-market")
    public ResponseEntity<Set<PieMarketTypeDto>> getMarketTypePercentages(){
        return new ResponseEntity<>(dashboardService.getMarketTypePercentages(), HttpStatus.OK);
    }

    @GetMapping("/pie-project")
    public ResponseEntity<Set<PieProjectTypeDto>> getProjectTypePercentages(){
        return new ResponseEntity<>(dashboardService.getProjectTypePercentages(), HttpStatus.OK);
    }

    @GetMapping("/histogram-feedbacks")
    public ResponseEntity<Set<HistogramImpactDto>> getImpactFeedbackPercentages(JwtAuthenticationToken jwt){
        String username = jwt.getToken().getClaim("preferred_username");
        return new ResponseEntity<>(dashboardService.getImpactFeedbackPercentages(username), HttpStatus.OK);
    }


    @GetMapping("/histogram-ttm/{department}")
    public ResponseEntity<Set<HistogramProjectTtm>> getTtmProjects(@PathVariable("department") String department){
        return new ResponseEntity<>(dashboardService.getProjectsTtm(department), HttpStatus.OK);
    }



    @GetMapping("/pie-tracking-by-dates")
    public ResponseEntity<Set<PieTrackingDto>> getTrackingPercentages(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam("department") String department
    ){
        return new ResponseEntity<>(dashboardService.getTrackingPercentagesByDates(fromDate, toDate, department), HttpStatus.OK);
    }


    @GetMapping("/pie-market-by-dates")
    public ResponseEntity<Set<PieMarketTypeDto>> getMarketTypePercentagesByDates(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam("department") String department
    ){
        return new ResponseEntity<>(dashboardService.getMarketTypePercentagesByDates(fromDate, toDate, department), HttpStatus.OK);
    }


    @GetMapping("/pie-project-by-dates")
    public ResponseEntity<Set<PieProjectTypeDto>> getProjectTypePercentagesByDates(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam("department") String department
    ){
        return new ResponseEntity<>(dashboardService.getProjectTypePercentagesByDates(fromDate, toDate, department), HttpStatus.OK);
    }


    @GetMapping("/histogram-ttm-by-dates")
    public ResponseEntity<Set<HistogramProjectTtm>> getTtmProjectsByDates(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam("department") String department
    ){
        return new ResponseEntity<>(dashboardService.getProjectsTtmByDates(fromDate, toDate, department), HttpStatus.OK);
    }
}
