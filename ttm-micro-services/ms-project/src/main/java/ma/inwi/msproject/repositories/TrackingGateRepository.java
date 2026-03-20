package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.entities.TrackingGate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TrackingGateRepository extends JpaRepository<TrackingGate, Long> {

    Set<TrackingGate> findByTracking(Tracking tracking);
}
