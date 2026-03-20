package ma.inwi.msproject.repositories;

import feign.Param;
import ma.inwi.msproject.dto.ActionDto;
import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.entities.GateProject;
import ma.inwi.msproject.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Query("SELECT gp FROM GateProject gp WHERE gp.project.id = :projectId")
    List<GateProject> findGateProjectsByProjectId(Long projectId);

    @Query("SELECT p FROM Project p WHERE p.title= :title")
    Optional<Project> findByTitle(@Param("title") String title);

    @Query("SELECT p FROM Project p WHERE p.ownerUsername= :ownerUsername")
    List<Project> findProjectsByOwnerUsername(@Param("ownerUsername") String ownerUsername);

    @Query("SELECT p FROM Project p JOIN p.departments d WHERE d = :department")
    List<Project> findProjectsByDepartment(@Param("department") String department);


}

