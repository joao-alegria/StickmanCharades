package pt.ua.deti.es.g54.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser creator;
    
    @Column
    private String title;
    
    @Column
    private Integer durationSeconds;
    
    @Column
    private Boolean isActive;
    
    @Column
    private Boolean isAvailable;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,mappedBy="sessionInPlay")
    private Set<DBUser> players = new HashSet();
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,mappedBy="targetSession")
    private Set<DBEvent> events = new HashSet();
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,mappedBy="targetSession")
    private Set<DBCommand> commands = new HashSet();
    
    @ElementCollection
    private List<String> words;

    public DBSession() {
    }
    
    public DBSession(String title, Integer durationSeconds, DBUser creator, List<String> words) {
        this.title=title;
        this.durationSeconds=durationSeconds;
        this.creator = creator;
        this.isActive=false;
        this.isAvailable=true;
        players.add(creator);
        creator.setSessionInPlay(this);
        this.words=words;
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

    public void addPlayer(DBUser user){
        players.add(user);
    }
    
    public void removePlayer(DBUser user){
        players.remove(user);
    }
    
    public Set<DBUser> getPlayers() {
        return players;
    }
    
    public String getRandomWord(){
        int random=(int)(Math.random()*this.words.size());
        return this.words.get(random);
    }
    
}
