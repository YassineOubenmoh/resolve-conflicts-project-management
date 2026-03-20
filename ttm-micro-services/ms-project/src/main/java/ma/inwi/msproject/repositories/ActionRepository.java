package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.Action;
import ma.inwi.msproject.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ActionRepository extends JpaRepository<Action, Long> {

    @Query("SELECT proj FROM Action a " +
            "JOIN a.requiredAction r " +
            "JOIN r.departementGateProject dg " +
            "JOIN dg.gateProject g " +
            "JOIN g.project proj " +
            "WHERE r.id = :requiredActionId")
    Project findProjectByRequiredAction(@Param("requiredActionId") Long requiredActionId);

    @Query("SELECT a FROM Action a WHERE a.actionLabel= :actionLabel")
    Action findActionByImpact(@Param("actionLabel") String actionLabel);

    @Query("""
    SELECT a FROM Action a 
    WHERE a.actionCreatedBy = :actionCreatedBy
    """)
    List<Action> findImpactsByInterlocutor(@Param("actionCreatedBy") String actionCreatedBy);


    @Query("SELECT a FROM Action a WHERE a.requiredAction.id = :requiredActionId")
    List<Action> findActionsByRequiredActionId(@Param("requiredActionId") Long requiredActionId);


}