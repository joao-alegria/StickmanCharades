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
public interface UserRepository extends JpaRepository<DBUser, Long>{

    @Query(value = "SELECT COUNT(u) FROM DBUser u WHERE u.username = :username")
    long getUsernameCount(@Param("username") String username);

    @Query(value = "SELECT COUNT(u) FROM DBUser u WHERE u.email = :email")
    long getEmailCount(@Param("email") String email);

    @Query(value = "SELECT u FROM DBUser u WHERE u.username = :username")
    public List<DBUser> getUserByUsername(@Param("username") String username);
    
    @Query(value = "SELECT g from DBRole g WHERE g.name = 'ROLE_USER'")
    public List<DBRole> getRole();

}
