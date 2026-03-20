package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.enums.GateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GateRepository extends JpaRepository<Gate, Long> {

    @Query("SELECT g FROM Gate g WHERE g.gateType= :gateType")
    Optional<Gate> findByGateType(@Param("gateType") GateType gateType);
}
