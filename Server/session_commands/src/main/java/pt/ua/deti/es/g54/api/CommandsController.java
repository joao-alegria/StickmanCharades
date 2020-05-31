/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.api;

import java.security.Principal;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.es.g54.services.CommandsService;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class CommandsController {

    private static Logger logger = LoggerFactory.getLogger(CommandsController.class);
    
    @Autowired
    private CommandsService cs;
    
    @GetMapping(value="/session/{sessionId}/action/start")
    public JSONObject startSession(@PathVariable String sessionId,Principal principal){
        logger.info("Start session command received");
        JSONObject command = new JSONObject();
        command.put("command", "startSession");
        command.put("session", "esp54_"+sessionId);
        command.put("username", principal.getName());
        System.out.println(principal.getName());
        return cs.processCommand(command);
    }
    
    @GetMapping(value="/session/{sessionId}/action/stop")
    public JSONObject stopSession(@PathVariable String sessionId, Principal principal){
        logger.info("Stop session command received");
        JSONObject command = new JSONObject();
        command.put("command", "stopSession");
        command.put("session", "esp54_"+sessionId);
        command.put("username", principal.getName());
        return cs.processCommand(command);
    }
    
    @GetMapping(value="/session/{sessionId}/action/notifyAdmin")
    public JSONObject notifyAdmin(@PathVariable String sessionId, Principal principal){
        logger.info("Notify admin command received");
        JSONObject command = new JSONObject();
        command.put("command", "notifyAdmin");
        command.put("session", "esp54_"+sessionId);
        command.put("username", principal.getName());
        return cs.processCommand(command);
    }
}
