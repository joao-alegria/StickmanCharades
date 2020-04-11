package es_g54.repository;

import es_g54.entities.DBGameInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface GameInviteRepository extends JpaRepository<DBGameInvite, Long>{

}
