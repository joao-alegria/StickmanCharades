package es_g54.repository;

import es_g54.entities.DBGroup;
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
public interface UserRepository extends JpaRepository<DBUser, Long>{

    @Query(value = "SELECT u FROM DBUser u WHERE u.username = :username")
    public DBUser getUserByUsername(@Param("username") String username);
    
    @Query(value = "SELECT g from DBGroup g WHERE g.name = 'user'")
    public DBGroup getGroup();

}
