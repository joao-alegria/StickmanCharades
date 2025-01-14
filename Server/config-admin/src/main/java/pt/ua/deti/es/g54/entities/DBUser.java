package pt.ua.deti.es.g54.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author joaoalegria
 */
@Entity
public class DBUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    
    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private boolean enabled = true;
    
    @ManyToMany(mappedBy= "users", cascade = CascadeType.MERGE)
    private Set<DBRole> roles;

    @ManyToOne
    @JoinColumn
    private DBSession sessionInPlay;

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="ME_FRIEND",
            joinColumns={@JoinColumn(name="ME_ID")},
            inverseJoinColumns={@JoinColumn(name="FRIEND_ID")})
    private Set<DBUser> byMe = new HashSet<>();

    @ManyToMany(mappedBy="byMe")
    private Set<DBUser> byOthers = new HashSet<>();
    
    @OneToMany(
        mappedBy = "creator",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DBSession> mySessions = new ArrayList<>();

    @OneToMany(mappedBy="eventCreator")
    private Set<DBEvent> events = new HashSet<>();
    
    @OneToMany(mappedBy="commandCreator")
    private Set<DBCommand> commands = new HashSet<>();

    public DBUser() {}

    public DBUser(String email, String password) {
        this.username = "admin";
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRoles(Set<DBRole> roles) {
        this.roles = roles;
    }

    public void addRole(DBRole role) {
        roles.add(role);
    }

    public Set<DBUser> getFriends() {
        Set<DBUser> mergedSet=new HashSet<>();
        mergedSet.addAll(byMe);
        mergedSet.addAll(byOthers);
        return mergedSet;
    }
    
    public void addFriend(DBUser friend){
        byMe.add(friend);
    }
    
    public void removeFriend(DBUser friend){
        byMe.remove(friend);
    }

    public DBSession getSessionInPlay() {
        return sessionInPlay;
    }

    public void setSessionInPlay(DBSession sessionInPlay) {
        this.sessionInPlay = sessionInPlay;
    }

    public boolean isAdmin() {
        for (DBRole role : roles) {
            if (role.getName().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}