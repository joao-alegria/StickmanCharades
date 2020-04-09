package es_g54.repository;

import es_g54.entities.DBSession;
import es_g54.entities.DBUser;
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

    @Query(value = "SELECT u FROM DBSession u WHERE u.id = :id")
    public DBSession getSessionById(@Param("id") Long id);
    
}
