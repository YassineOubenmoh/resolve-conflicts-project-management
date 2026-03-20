package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.GateProjectDto;
import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.DecisionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class GateProjectMapperTest {

    private GateProjectMapper gateProjectMapper;

    @BeforeEach
    void setUp() {
        gateProjectMapper = Mappers.getMapper(GateProjectMapper.class);
    }

    @Test
    void testGateProjectToGateProjectDto() {
        Project project = Project.builder().id(2L).build();
        DepartementGateProject departementGateProject1 = DepartementGateProject.builder().id(2L).build();
        DepartementGateProject departementGateProject2 = DepartementGateProject.builder().id(3L).build();
        TrackingGate trackingGate = TrackingGate.builder().id(1L).build();
        LocalDateTime now = LocalDateTime.now();

        GateProject gateProject = GateProject.builder()
                .id(1L)
                .trackingGate(trackingGate)
                .currentGate(false)
                .passingDate(now)
                .inProgress(true)
                .startingGate(true)
                .decisionType(DecisionType.GO)
                .information(Set.of("Information 1", "Information 2"))
                .actions(Set.of("Action 1", "Action 2"))
                .decisions(Set.of("Decision 1", "Decision 2"))
                .project(project)
                .departementGateProjects(Set.of(departementGateProject1, departementGateProject2))
                .build();

        GateProjectDto expectedDto = GateProjectDto.builder()
                .id(1L)
                .trackingGateId(1L)
                .currentGate(false)
                .passingDate(now)
                .inProgress(true)
                .startingGate(true)
                .decisionType(DecisionType.GO)
                .information(Set.of("Information 1", "Information 2"))
                .actions(Set.of("Action 1", "Action 2"))
                .decisions(Set.of("Decision 1", "Decision 2"))
                .projectId(2L)
                .departementGateProjectIds(Set.of(2L, 3L))
                .build();

        GateProjectDto result = gateProjectMapper.gateProjectToGateProjectDto(gateProject);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void testGateProjectDtoToGateProject() {
        GateProjectDto gateProjectDto = GateProjectDto.builder()
                .id(1L)
                .trackingGateId(1L)
                .currentGate(false)
                .passingDate(LocalDateTime.now())
                .inProgress(true)
                .startingGate(true)
                .decisionType(DecisionType.GO)
                .information(Set.of("Information 1", "Information 2"))
                .actions(Set.of("Action 1", "Action 2"))
                .decisions(Set.of("Decision 1", "Decision 2"))
                .projectId(2L)
                .departementGateProjectIds(Set.of(2L, 3L))
                .build();

        GateProject result = gateProjectMapper.gateProjectDtoToGateProject(gateProjectDto);

        assertNotNull(result);
        assertEquals(gateProjectDto.getId(), result.getId());
        assertEquals(gateProjectDto.getTrackingGateId(), result.getTrackingGate().getId());
        assertEquals(gateProjectDto.getProjectId(), result.getProject().getId());
        assertEquals(gateProjectDto.getDepartementGateProjectIds().size(), result.getDepartementGateProjects().size());
    }

    @Test
    void testGateProjectToGateProjectDto_NullValues() {
        GateProject gateProject = new GateProject();
        GateProjectDto result = gateProjectMapper.gateProjectToGateProjectDto(gateProject);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTrackingGateId());
        assertNull(result.getProjectId());
        assertNull(result.getDepartementGateProjectIds());
    }


}
