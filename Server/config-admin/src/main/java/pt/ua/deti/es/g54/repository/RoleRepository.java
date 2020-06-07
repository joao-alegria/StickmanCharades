package pt.ua.deti.es.g54.repository;

import pt.ua.deti.es.g54.entities.DBRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author joaoalegria
 */
@Repository
public interface RoleRepository extends JpaRepository<DBRole, Long>{

}
