package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.DepartementGateProject;
import ma.inwi.msproject.entities.GateProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DepartementGateProjectRepository extends JpaRepository<DepartementGateProject, Long> {

    @Query("""
    SELECT dgp FROM DepartementGateProject dgp
    WHERE dgp.departement.departement = :department
      AND dgp.gateProject.project.id = :projectId
      AND dgp.deleted = false
      AND dgp.gateProject.deleted = false
    """)
    Set<DepartementGateProject> findGateProjectsByDepartmentAndProjectId(
            @Param("department") String department,
            @Param("projectId") Long projectId
    );


    @Query("""
    SELECT dgp FROM DepartementGateProject dgp
    WHERE dgp.departement.departement = :department
      AND dgp.deleted = false
      AND dgp.gateProject.deleted = false
    """)
    Set<DepartementGateProject> findGateProjectsByDepartment(
            @Param("department") String department
    );

}

