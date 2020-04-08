package es_g54.entities;

import es_g54.api.entities.UserData;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
public class DBUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private byte[] salt;

    @Column
    private byte[] password;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
    private Set<DBRole> groups;

    public DBUser() {}

    public DBUser(UserData userData, byte[] salt, byte[] password) {
        this.username = userData.getUsername();
        this.email = userData.getEmail();
        this.salt = salt;
        this.password = password;
        this.groups = new HashSet<>();
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

        for (DBRole group : groups) {
            stringGroups.add(group.getName());

        }
        return stringGroups;
    }

    public void setGroups(Set<DBRole> groups) {
        this.groups = groups;
    }

    public void addGroup(DBRole group) {
        groups.add(group);
    }
}
