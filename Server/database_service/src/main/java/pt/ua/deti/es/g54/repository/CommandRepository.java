package pt.ua.deti.es.g54.repository;

import pt.ua.deti.es.g54.entities.DBGameInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.es.g54.entities.DBCommand;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface CommandRepository extends JpaRepository<DBCommand, Long>{

}
