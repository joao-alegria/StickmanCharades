package pt.ua.deti.es.g54.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.es.g54.entities.DBEvent;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface EventRepository extends JpaRepository<DBEvent, Long>{

}
