/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.entities;

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
public class DBEvent implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private DBUser commandCreator;
    
    @Column
    private DBSession targetSession;
    
    @Column
    private String event;
    
    @Column
    private Long time;

    public DBEvent() {
    }

    public DBEvent(DBUser commandCreator, DBSession targetSession, String event, Long time) {
        this.commandCreator = commandCreator;
        this.targetSession = targetSession;
        this.event=event;
        this.time=time;
    }
    
    
    
}
