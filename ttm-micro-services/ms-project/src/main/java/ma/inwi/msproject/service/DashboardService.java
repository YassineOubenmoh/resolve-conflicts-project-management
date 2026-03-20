package ma.inwi.msproject.service;

import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.dto.dashboard.*;
import ma.inwi.msproject.dto.frontdtos.GateProjectFront;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.entities.RequiredAction;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.enums.TrackingType;
import ma.inwi.msproject.enums.ValidationStatus;
import ma.inwi.msproject.exceptions.RequiredActionNotFoundException;
import ma.inwi.msproject.mappers.ProjectMapper;
import ma.inwi.msproject.repositories.ProjectRepository;
import ma.inwi.msproject.repositories.RequiredActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final ProjectService projectService;
    private final TrackingService trackingService;
    private final ActionService actionService;
    private final GateProjectService gateProjectService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final RequiredActionRepository requiredActionRepository;


    public DashboardService(ProjectService projectService, TrackingService trackingService, ActionService actionService, GateProjectService gateProjectService, ProjectRepository projectRepository, ProjectMapper projectMapper, RequiredActionRepository requiredActionRepository) {
        this.projectService = projectService;
        this.trackingService = trackingService;
        this.actionService = actionService;
        this.gateProjectService = gateProjectService;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.requiredActionRepository = requiredActionRepository;
    }


    /*
    public int countProjectsByDates(Date fromDate, Date toDate, String department){
        List<Project> projects = projectRepository.findProjectsByDepartment(department);

    }

     */


    public Set<PieTrackingDto> getTrackingPercentages() {
        Set<ProjectDto> projectDtos = projectService.getAllProjects();

        int fullTrackCount = 0;
        int fastTrackCount = 0;
        int superTrackCount = 0;
        int hyperTrackCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            TrackingDto trackingDto = trackingService.getTrackingById(projectDto.getTrackingId());
            if (trackingDto != null && trackingDto.getTrackingType() != null) {
                total++;
                switch (trackingDto.getTrackingType()) {
                    case FULL_TRACK -> fullTrackCount++;
                    case FAST_TRACK -> fastTrackCount++;
                    case SUPER_F_TRACK -> superTrackCount++;
                    case HYPER_F_TRACK -> hyperTrackCount++;
                }
            }
        }

        Set<PieTrackingDto> pieTrackingDtos = new HashSet<>();

        if (total > 0) {
            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FULL_TRACK)
                    .trackingPercentage((double) fullTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FAST_TRACK)
                    .trackingPercentage((double) fastTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.SUPER_F_TRACK)
                    .trackingPercentage((double) superTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.HYPER_F_TRACK)
                    .trackingPercentage((double) hyperTrackCount * 100 / total)
                    .build());
        }

        return pieTrackingDtos;
    }




    /*
    public Set<PieTrackingDto> getTrackingPercentagesBetweenDates(Date fromDate, Date toDate) {
        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate);

        int fullTrackCount = 0;
        int fastTrackCount = 0;
        int superTrackCount = 0;
        int hyperTrackCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            TrackingDto trackingDto = trackingService.getTrackingById(projectDto.getTrackingId());
            if (trackingDto != null && trackingDto.getTrackingType() != null) {
                total++;
                switch (trackingDto.getTrackingType()) {
                    case FULL_TRACK -> fullTrackCount++;
                    case FAST_TRACK -> fastTrackCount++;
                    case SUPER_F_TRACK -> superTrackCount++;
                    case HYPER_F_TRACK -> hyperTrackCount++;
                }
            }
        }

        Set<PieTrackingDto> pieTrackingDtos = new HashSet<>();

        if (total > 0) {
            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FULL_TRACK)
                    .trackingPercentage((double) fullTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FAST_TRACK)
                    .trackingPercentage((double) fastTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.SUPER_F_TRACK)
                    .trackingPercentage((double) superTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.HYPER_F_TRACK)
                    .trackingPercentage((double) hyperTrackCount * 100 / total)
                    .build());
        }

        return pieTrackingDtos;
    }

     */



    public Set<PieMarketTypeDto> getMarketTypePercentages(){
        Set<ProjectDto> projectDtos = projectService.getAllProjects();

        int prepaidCount = 0;
        int postpaidCount = 0;
        int homeCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            if (projectDto.getMarketType() != null) {
                total++;
                switch (projectDto.getMarketType()) {
                    case "Prépayé" -> prepaidCount++;
                    case "Home" -> postpaidCount++;
                    case "Postpayé" -> homeCount++;
                }
            }
        }

        Set<PieMarketTypeDto> pieMarketTypeDtos = new HashSet<>();

        if (total > 0) {
            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Prepayé")
                    .marketTypePercentage((double) prepaidCount * 100 / total)
                    .build());

            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Postpayé")
                    .marketTypePercentage((double) postpaidCount * 100 / total)
                    .build());

            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Home")
                    .marketTypePercentage((double) homeCount * 100 / total)
                    .build());
        }

        return pieMarketTypeDtos;
    }


    public Set<PieProjectTypeDto> getProjectTypePercentages(){
        Set<ProjectDto> projectDtos = projectService.getAllProjects();

        int commercialCodirCount = 0;
        int committeeTTMCount = 0;
        int committeeTechnicalCount = 0;
        int committeeInnovationCount = 0;
        int committeeMarketingCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            if (projectDto.getProjectType() != null) {
                total++;
                switch (projectDto.getProjectType()) {
                    case "Codir Go-To-Market" -> commercialCodirCount++;
                    case "Comité TTM" -> committeeTTMCount++;
                    case "Comité Technique" -> committeeTechnicalCount++;
                    case "Comité Innovation" -> committeeInnovationCount++;
                    case "Comité Marketing" -> committeeMarketingCount++;
                }
            }
        }

        Set<PieProjectTypeDto> pieProjectTypeDtos = new HashSet<>();

        if (total > 0) {
            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Codir Go-To-Market")
                    .projectTypePercentage((double) commercialCodirCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité TTM")
                    .projectTypePercentage((double) committeeTTMCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Technique")
                    .projectTypePercentage((double) committeeTechnicalCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Innovation")
                    .projectTypePercentage((double) committeeInnovationCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Marketing")
                    .projectTypePercentage((double) committeeMarketingCount * 100 / total)
                    .build());
        }

        return pieProjectTypeDtos;
    }


    public Set<HistogramImpactDto> getImpactFeedbackPercentages(String username){
        Set<ActionDto> actionDtos = actionService.getImpactAndReturnsByInterlocutor(username);


        int impactAcceptedCount = 0;
        int impactRejectedCount = 0;
        int impactToModifyCount = 0;
        int total = 0;

        for (ActionDto actionDto : actionDtos) {
            if (actionDto.getValidationStatus() != null) {
                total++;
                switch (actionDto.getValidationStatus()) {
                    case ValidationStatus.ACCEPTER -> impactAcceptedCount++;
                    case ValidationStatus.REFUSER -> impactRejectedCount++;
                    case ValidationStatus.A_MODIFIER -> impactToModifyCount++;
                }
            }
        }


        Set<HistogramImpactDto> histogramImpactDtos = new HashSet<>();

        if (total > 0) {
            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("Accepted")
                    .impactFeedbackPercentage((double) impactAcceptedCount * 100 / total)
                    .build());

            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("Rejected")
                    .impactFeedbackPercentage((double) impactRejectedCount * 100 / total)
                    .build());

            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("To Modify")
                    .impactFeedbackPercentage((double) impactToModifyCount * 100 / total)
                    .build());
        }

        return histogramImpactDtos;
    }



    public Set<HistogramProjectTtm> getProjectsTtm(String department) {
        if (department == null || department.isBlank()) {
            return Collections.emptySet();
        }

        Set<Project> projects = gateProjectService.getCompletedProjects(department);
        if (projects == null || projects.isEmpty()) {
            return Collections.emptySet();
        }

        Set<HistogramProjectTtm> histogramProjectTtms = new HashSet<>();
        for (Project project : projects) {
            if (project != null && project.getId() != null) {
                Long ttm = projectService.calculateTtm(project.getId());
                HistogramProjectTtm histogramProjectTtm = HistogramProjectTtm.builder()
                        .projectTitle(project.getTitle())
                        .ttm(ttm)
                        .build();
                histogramProjectTtms.add(histogramProjectTtm);
            }
        }

        return histogramProjectTtms;
    }











    public Set<ProjectDto> getProjectsFromDateBeginningToEnd(Date fromDate, Date toDate, String department) {
        List<Project> projects = projectRepository.findProjectsByDepartment(department);
        Set<Project> resultProjects = new HashSet<>();

        for (Project project : projects) {
            Date startDate = project.getDateStartTtm();
            if (startDate != null && !startDate.before(fromDate) && !startDate.after(toDate)) {
                resultProjects.add(project);
            }
        }

        return resultProjects.stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());
    }


    public Set<PieTrackingDto> getTrackingPercentagesByDates(Date fromDate, Date toDate, String department) {
        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate, department);


        int fullTrackCount = 0;
        int fastTrackCount = 0;
        int superTrackCount = 0;
        int hyperTrackCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            TrackingDto trackingDto = trackingService.getTrackingById(projectDto.getTrackingId());
            if (trackingDto != null && trackingDto.getTrackingType() != null) {
                total++;
                switch (trackingDto.getTrackingType()) {
                    case FULL_TRACK -> fullTrackCount++;
                    case FAST_TRACK -> fastTrackCount++;
                    case SUPER_F_TRACK -> superTrackCount++;
                    case HYPER_F_TRACK -> hyperTrackCount++;
                }
            }
        }

        Set<PieTrackingDto> pieTrackingDtos = new HashSet<>();

        if (total > 0) {
            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FULL_TRACK)
                    .trackingPercentage((double) fullTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.FAST_TRACK)
                    .trackingPercentage((double) fastTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.SUPER_F_TRACK)
                    .trackingPercentage((double) superTrackCount * 100 / total)
                    .build());

            pieTrackingDtos.add(PieTrackingDto.builder()
                    .trackingType(TrackingType.HYPER_F_TRACK)
                    .trackingPercentage((double) hyperTrackCount * 100 / total)
                    .build());
        }

        return pieTrackingDtos;
    }



    public Set<PieMarketTypeDto> getMarketTypePercentagesByDates(Date fromDate, Date toDate, String department){
        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate, department);

        int prepaidCount = 0;
        int postpaidCount = 0;
        int homeCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            if (projectDto.getMarketType() != null) {
                total++;
                switch (projectDto.getMarketType()) {
                    case "Prépayé" -> prepaidCount++;
                    case "Home" -> postpaidCount++;
                    case "Postpayé" -> homeCount++;
                }
            }
        }

        Set<PieMarketTypeDto> pieMarketTypeDtos = new HashSet<>();

        if (total > 0) {
            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Prepayé")
                    .marketTypePercentage((double) prepaidCount * 100 / total)
                    .build());

            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Postpayé")
                    .marketTypePercentage((double) postpaidCount * 100 / total)
                    .build());

            pieMarketTypeDtos.add(PieMarketTypeDto.builder()
                    .marketType("Home")
                    .marketTypePercentage((double) homeCount * 100 / total)
                    .build());
        }

        return pieMarketTypeDtos;
    }



    public Set<PieProjectTypeDto> getProjectTypePercentagesByDates(Date fromDate, Date toDate, String department){
        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate, department);

        int commercialCodirCount = 0;
        int committeeTTMCount = 0;
        int committeeTechnicalCount = 0;
        int committeeInnovationCount = 0;
        int committeeMarketingCount = 0;
        int total = 0;

        for (ProjectDto projectDto : projectDtos) {
            if (projectDto.getProjectType() != null) {
                total++;
                switch (projectDto.getProjectType()) {
                    case "Codir Go-To-Market" -> commercialCodirCount++;
                    case "Comité TTM" -> committeeTTMCount++;
                    case "Comité Technique" -> committeeTechnicalCount++;
                    case "Comité Innovation" -> committeeInnovationCount++;
                    case "Comité Marketing" -> committeeMarketingCount++;
                }
            }
        }

        Set<PieProjectTypeDto> pieProjectTypeDtos = new HashSet<>();

        if (total > 0) {
            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Codir Go-To-Market")
                    .projectTypePercentage((double) commercialCodirCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité TTM")
                    .projectTypePercentage((double) committeeTTMCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Technique")
                    .projectTypePercentage((double) committeeTechnicalCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Innovation")
                    .projectTypePercentage((double) committeeInnovationCount * 100 / total)
                    .build());

            pieProjectTypeDtos.add(PieProjectTypeDto.builder()
                    .projectType("Comité Marketing")
                    .projectTypePercentage((double) committeeMarketingCount * 100 / total)
                    .build());
        }

        return pieProjectTypeDtos;
    }



    public Set<HistogramProjectTtm> getProjectsTtmByDates(Date fromDate, Date toDate, String department) {
        if (department == null || department.isBlank()) {
            return Collections.emptySet();
        }

        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate, department);
        if (projectDtos == null || projectDtos.isEmpty()) {
            return Collections.emptySet();
        }

        // Extract IDs from projectDtos
        Set<Long> projectDtoIds = projectDtos.stream()
                .filter(dto -> dto != null && dto.getId() != null)
                .map(ProjectDto::getId)
                .collect(Collectors.toSet());

        // Get completed projects and filter them
        Set<Project> allCompletedProjects = gateProjectService.getCompletedProjects(department);
        if (allCompletedProjects == null || allCompletedProjects.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Project> filteredProjects = allCompletedProjects.stream()
                .filter(project -> project != null && project.getId() != null && projectDtoIds.contains(project.getId()))
                .collect(Collectors.toSet());

        Set<HistogramProjectTtm> histogramProjectTtms = new HashSet<>();
        for (Project project : filteredProjects) {
            Long ttm = projectService.calculateTtm(project.getId());
            HistogramProjectTtm histogram = HistogramProjectTtm.builder()
                    .projectTitle(project.getTitle())
                    .ttm(ttm)
                    .build();
            histogramProjectTtms.add(histogram);
        }

        return histogramProjectTtms;
    }



    public Set<HistogramImpactDto> getImpactFeedbackPercentages(String username, Date fromDate, Date toDate, String department){
        Set<ActionDto> actionDtos = actionService.getImpactAndReturnsByInterlocutor(username);
        Set<Project> projects = new HashSet<>();

        for (ActionDto actionDto : actionDtos){
            Long requiredActionId = actionDto.getRequiredActionId();
            RequiredAction requiredAction = requiredActionRepository.findById(requiredActionId).orElseThrow(
                    () -> new RequiredActionNotFoundException("Required action not found !"));
            Project project = requiredAction.getDepartementGateProject().getGateProject().getProject();
            projects.add(project);
        }

        Set<ProjectDto> projectConcernedDtos = projects.stream()
                .map(projectMapper::projectToProjectDto)
                .collect(Collectors.toSet());

        Set<ProjectDto> projectDtos = getProjectsFromDateBeginningToEnd(fromDate, toDate, department);



        int impactAcceptedCount = 0;
        int impactRejectedCount = 0;
        int impactToModifyCount = 0;
        int total = 0;

        for (ActionDto actionDto : actionDtos) {
            if (actionDto.getValidationStatus() != null) {
                total++;
                switch (actionDto.getValidationStatus()) {
                    case ValidationStatus.ACCEPTER -> impactAcceptedCount++;
                    case ValidationStatus.REFUSER -> impactRejectedCount++;
                    case ValidationStatus.A_MODIFIER -> impactToModifyCount++;
                }
            }
        }


        Set<HistogramImpactDto> histogramImpactDtos = new HashSet<>();

        if (total > 0) {
            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("Accepted")
                    .impactFeedbackPercentage((double) impactAcceptedCount * 100 / total)
                    .build());

            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("Rejected")
                    .impactFeedbackPercentage((double) impactRejectedCount * 100 / total)
                    .build());

            histogramImpactDtos.add(HistogramImpactDto.builder()
                    .impactFeedback("To Modify")
                    .impactFeedbackPercentage((double) impactToModifyCount * 100 / total)
                    .build());
        }

        return histogramImpactDtos;
    }


}
