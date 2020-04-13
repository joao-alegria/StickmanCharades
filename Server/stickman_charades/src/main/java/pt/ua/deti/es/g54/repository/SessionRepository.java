package pt.ua.deti.es.g54.repository;

import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
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

    @Query(value = "SELECT s FROM DBSession as s WHERE s.id = :id")
    public List<DBSession> getSessionById(@Param("id") Long id);

//    @Query(value = "SELECT s FROM DBSession s INNER JOIN DBUser u ON s.creator=u.id WHERE u.username != :name AND  s.isAvailable=true" )
    @Query(value = "SELECT s FROM DBSession s WHERE s.isAvailable=true" )
    public List<DBSession> getAllSessions(@Param("name") String name);
    
}
