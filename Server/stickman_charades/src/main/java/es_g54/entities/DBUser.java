package es_g54.entities;

import es_g54.api.entities.UserData;

import java.io.Serializable;
import java.util.HashSet;
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
    
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<DBRole> roles;

    @ManyToOne
    @JoinColumn
    private DBSession sessionInPlay;

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="ME_FRIEND",
            joinColumns={@JoinColumn(name="ME_ID")},
            inverseJoinColumns={@JoinColumn(name="FRIEND_ID")})
    private Set<DBUser> byMe = new HashSet<DBUser>();

    @ManyToMany(mappedBy="byMe")
    private Set<DBUser> byOthers = new HashSet<DBUser>();

    public DBUser() {}

    public DBUser(UserData userData, String password) {
        this.username = userData.getUsername();
        this.email = userData.getEmail();
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
        Set<DBUser> mergedSet=new HashSet();
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
    
    
}