package es_g54.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    
    @ManyToOne
    @JoinColumn
    private DBUser myself;
    
    @OneToMany(cascade=ALL, mappedBy="myself")
    private Set<DBUser> friends;

    @Column(unique = true)
    private String email;

    @Column
    private byte[] salt;

    @Column
    private byte[] password;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
    private Set<DBGroup> groups;

    public DBUser() {}

    public DBUser(String username, String email, byte[] salt, byte[] password) {
        this.username = username;
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.groups = new HashSet<>();
        this.myself=this;
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
        return friends;
    }
    
    public void addFriend(DBUser friend){
        friends.add(friend);
    }
    
    public void removeFriend(DBUser friend){
        friends.remove(friend);
    }
}