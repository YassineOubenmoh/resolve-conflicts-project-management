package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.GateProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GateProjectRepository extends JpaRepository<GateProject, Long> {
    Set<GateProject> findGateProjectsByProjectId(Long projectId);

    List<GateProject> findByProjectId(Long projectId);

    @Modifying
    @Query("UPDATE GateProject gp SET gp.projectCompleted = true WHERE gp.id = :id")
    void markProjectCompleted(@Param("id") Long id);


}
