package pt.ua.deti.es.g54.repository;

import pt.ua.deti.es.g54.entities.DBGameInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface GameInviteRepository extends JpaRepository<DBGameInvite, Long>{

}
