package pt.ua.deti.es.g54.repository;

import pt.ua.deti.es.g54.entities.DBRole;
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
public interface RoleRepository extends JpaRepository<DBRole, Long>{

}
