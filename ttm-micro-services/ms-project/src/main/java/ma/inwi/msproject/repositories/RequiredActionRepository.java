package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.RequiredAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequiredActionRepository extends JpaRepository<RequiredAction, Long> {

    @Query("SELECT r FROM RequiredAction r WHERE r.requiredAction= :requiredAction AND r.departementGateProject.id= :departementGateProjectId")
    Optional<RequiredAction> findRequiredActionAndDepartementGateProject(@Param("requiredAction") String requiredAction, @Param("departementGateProjectId") Long id);

    @Query("SELECT r FROM RequiredAction r " +
            "JOIN r.departementGateProject dg " +
            "JOIN dg.gateProject gp " +
            "JOIN gp.project p " +
            "WHERE p.id = :projectId")
    List<RequiredAction> findRequiredActionByProjectId(@Param("projectId") Long projectId);


    @Query("""
    SELECT r FROM RequiredAction r WHERE r.requiredAction= :requiredActionLabel
    """)
    RequiredAction findRequiredActionIdByRequiredAction(@Param("requiredActionLabel") String requiredActionLabel);

}
