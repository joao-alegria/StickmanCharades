package es_g54.repository;

import es_g54.entities.DBSession;
import es_g54.entities.DBUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface SessionRepository extends JpaRepository<DBSession, Long>{

    @Query(value = "SELECT u FROM DBSession u WHERE u.id = :id AND u.creator=:name")
    public List<DBSession> getSessionById(@Param("name") String name, @Param("id") Long id);

    @Query(value = "SELECT u FROM DBSession u WHERE u.creator != :name AND u.isAvailable=true")
    public List<DBSession> getAllSessions(@Param("name") String name);
    
}
