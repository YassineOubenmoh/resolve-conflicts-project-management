package ma.inwi.ms_iam.repository;

import ma.inwi.ms_iam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   User findUserByUsername(String username);

   @Query("""
           SELECT DISTINCT u FROM User u
           JOIN u.roles r
           WHERE u.department = :department
           AND (r = 'INTERLOCUTEUR_SIGNALE_IMPACT' OR r = 'INTERLOCUTEUR_RETOUR_IMPACT')
           """)
   List<User> findInterlocutorsByDepartment(@Param("department") String department);

   @Query("""
    SELECT DISTINCT u FROM User u
    JOIN u.projectsId p
    JOIN u.roles r
    WHERE p = :projectId
      AND (r = 'INTERLOCUTEUR_SIGNALE_IMPACT' OR r = 'INTERLOCUTEUR_RETOUR_IMPACT')
    """)
   List<User> findInterlocutorsOfProject(@Param("projectId") Long projectId);


   @Query("""
    SELECT DISTINCT u FROM User u
    JOIN u.projectsId p
    JOIN u.roles r
    WHERE p = :projectId
      AND (r = 'SPOC')
    """)
   List<User> findReservedProjectBySpoc(@Param("projectId") Long projectId);



   @Query("""
    SELECT u FROM User u
    WHERE u.department = :department
    AND 'SPOC' IN elements(u.roles)
    """)
   User findSpocForDepartment(@Param("department") String department);



}
