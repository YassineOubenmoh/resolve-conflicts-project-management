package ma.inwi.msproject.repositories;

import ma.inwi.msproject.entities.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DepartementRepository extends JpaRepository<Departement, Long> {

    @Query("SELECT d FROM Departement d WHERE d.departement= :departement")
    Optional<Departement> findDepartementByLabel(@Param("departement") String departement);
}
