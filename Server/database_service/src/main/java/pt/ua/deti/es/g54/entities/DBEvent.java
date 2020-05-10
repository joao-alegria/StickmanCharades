/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author joaoalegria
 */
@Entity
public class DBEvent implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private DBUser eventCreator;
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "session_id")
    private DBSession targetSession;
    
    @Column
    private String event;
    
    @Column
    private Long time;

    public DBEvent() {
    }

    public DBEvent(DBUser eventCreator, DBSession targetSession, String event, Long time) {
        this.eventCreator = eventCreator;
        this.targetSession = targetSession;
        this.event=event;
        this.time=time;
    }
    
    
    
}
