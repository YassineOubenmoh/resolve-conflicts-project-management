package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.enums.TrackingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    @Query("SELECT t FROM Tracking t WHERE t.trackingType= :trackingType")
    Optional<Tracking> findByTrackingLabel(@Param("trackingTye")TrackingType trackingType);
}
