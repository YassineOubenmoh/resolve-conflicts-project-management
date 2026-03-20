package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.ConfigRequiredAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRequiredActionRepository extends JpaRepository<ConfigRequiredAction, Long> {

    ConfigRequiredAction findByDepartementIdAndGateId(Long departementId, Long gateId);

}
