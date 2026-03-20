package ma.inwi.msproject.mappers;

import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.entities.Project;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMapperTest {

    private final ProjectMapper mapper = ProjectMapper.INSTANCE;


    @Test
    void testProjectToProjectDtoWithNullGateProjects() {
        // Given
        Project project = Project.builder()
                .gateProjects(null)
                .build();

        // When
        ProjectDto dto = mapper.projectToProjectDto(project);

        // Then
        assertThat(dto.getGateProjectIds()).isNull();
    }

    @Test
    void testProjectDtoToProjectWithNullGateProjectIds() {
        // Given
        ProjectDto dto = ProjectDto.builder()
                .gateProjectIds(null)
                .build();

        // When
        Project project = mapper.projectDtoToProject(dto);

        // Then
        assertThat(project.getGateProjects()).isNull();
    }

    @Test
    void testProjectToProjectDtoWithEmptyGateProjects() {
        // Given
        Project project = Project.builder()
                .gateProjects(Set.of())
                .build();

        // When
        ProjectDto dto = mapper.projectToProjectDto(project);

        // Then
        assertThat(dto.getGateProjectIds()).isEmpty();
    }

    @Test
    void testProjectDtoToProjectWithEmptyGateProjectIds() {
        // Given
        ProjectDto dto = ProjectDto.builder()
                .gateProjectIds(Set.of())
                .build();

        // When
        Project project = mapper.projectDtoToProject(dto);

        // Then
        assertThat(project.getGateProjects()).isEmpty();
    }
}