package es_g54.repository;

import es_g54.entities.DBFriendInvite;
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
public interface FriendInviteRepository extends JpaRepository<DBFriendInvite, Long>{

    @Query(value = "SELECT u FROM DBSession u WHERE (u.inviteCreator.username = :username AND u.inviteTarget.username = :friendname) OR (u.inviteCreator.username = :friendname AND u.inviteTarget.username = :username) AND u.accepted=false")
    public List<DBFriendInvite> getInviteByUsernames(@Param("username") String username, @Param("friendname") String friendname);

}
