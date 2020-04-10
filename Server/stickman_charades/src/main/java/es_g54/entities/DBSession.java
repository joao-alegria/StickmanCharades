package es_g54.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author joaoalegria
 */
@Entity
public class DBSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private DBUser creator;
    
    @Column
    private String title;
    
    @Column
    private Integer durationSeconds;
    
    @Column
    private Boolean isActive;
    
    @Column
    private Boolean isAvailable;
    
    @OneToMany(mappedBy="sessionInPlay")
    private Set<DBUser> players;

    public DBSession(String title, Integer durationSeconds, DBUser creator) {
        this.title=title;
        this.durationSeconds=durationSeconds;
        this.creator = creator;
        this.isActive=false;
        this.isAvailable=true;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public DBUser getCreator() {
        return creator;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public Set<DBUser> getPlayers() {
        return players;
    }
    
}
