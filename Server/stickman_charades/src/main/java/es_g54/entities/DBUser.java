package es_g54.entities;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;
    
    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="ME_FRIEND",
            joinColumns={@JoinColumn(name="ME_ID")},
            inverseJoinColumns={@JoinColumn(name="FRIEND_ID")})
    private Set<DBUser> byMe = new HashSet<DBUser>();

    @ManyToMany(mappedBy="byMe")
    private Set<DBUser> byOthers = new HashSet<DBUser>();

    @Column(unique = true)
    private String email;

    @Column
    private byte[] salt;

    @Column
    private byte[] password;
    
    @ManyToOne
    @JoinColumn
    private DBSession sessionInPlay;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
    private Set<DBGroup> groups;

    public DBUser() {}

    public DBUser(String username, String email, byte[] salt, byte[] password) {
        this.username = username;
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.groups = new HashSet<>();
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

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Set<String> getGroups() {
        Set<String> stringGroups = new HashSet<>();

        for (DBGroup group : groups) {
            stringGroups.add(group.getName());

        }
        return stringGroups;
    }

    public void setGroups(Set<DBGroup> groups) {
        this.groups = groups;
    }

    public void addGroup(DBGroup group) {
        groups.add(group);
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