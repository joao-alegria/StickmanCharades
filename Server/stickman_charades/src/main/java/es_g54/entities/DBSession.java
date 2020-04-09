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
public class DBSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private DBUser creator;
    
    @Column
    private Boolean isActive;
    
    

    public DBSession(DBUser creator) {
        this.creator = creator;
        this.isActive=false;
    }

    public Long getId() {
        return id;
    }

    public DBUser getCreator() {
        return creator;
    }
    
    
}
