package es_g54.entities;

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
public class DBGameInvite implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private DBUser inviteCreator;
    
    @Column
    private DBUser inviteTarget;
    
    @Column
    private DBSession targetSession;
    
    @Column
    private Boolean isActive;

    public DBGameInvite(DBUser inviteCreator, DBUser inviteTarget, DBSession targetSession) {
        this.inviteCreator = inviteCreator;
        this.inviteTarget = inviteTarget;
        this.targetSession = targetSession;
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

    public DBSession getTargetSession() {
        return targetSession;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    
    
}
