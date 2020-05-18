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
public class DBCommand implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private DBUser commandCreator;
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "session_id")
    private DBSession targetSession;
    
    @Column
    private String commandMessage;
    
    @Column
    private String commandType;

    public DBCommand() {
    }

    public DBCommand(DBUser commandCreator, DBSession targetSession, String commandMessage, String commandType) {
        this.commandCreator = commandCreator;
        this.targetSession = targetSession;
        this.commandMessage = commandMessage;
        this.commandType = commandType;
    }
    
    
    
}
