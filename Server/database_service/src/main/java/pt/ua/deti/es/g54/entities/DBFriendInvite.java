package pt.ua.deti.es.g54.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author joaoalegria
 */
@Entity
public class DBFriendInvite implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private DBUser inviteCreator;
    
    @Column
    private DBUser inviteTarget;
    
    @Column
    private Boolean accepted;

    public DBFriendInvite() {
    }
    
    public DBFriendInvite(DBUser inviteCreator, DBUser inviteTarget) {
        this.inviteCreator = inviteCreator;
        this.inviteTarget = inviteTarget;
        this.accepted = false;
    }

    public Long getId() {
        return id;
    }

    public DBUser geInviteCreator() {
        return inviteCreator;
    }

    public DBUser getInviteTarget() {
        return inviteTarget;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
    
    

}
