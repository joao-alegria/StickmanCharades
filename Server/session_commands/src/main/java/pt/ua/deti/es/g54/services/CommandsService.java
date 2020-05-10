/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.services;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class CommandsService {
    
    private JSONParser jparser = new JSONParser();
    
    @Autowired
    private KafkaTemplate<String, String> kt;
    
    @KafkaListener(topics="esp54_commandsServiceTopic")
    private void receiveCommand(String command){
        try {
            JSONObject json=(JSONObject)jparser.parse(command);
            processCommand(json);
        } catch (ParseException ex) {
            Logger.getLogger(CommandsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public JSONObject processCommand(JSONObject json){
        JSONObject message;
        switch((String)json.get("command")){
            case "notifyAdmin":
                message=new JSONObject();
                message.put("msg", "Notification forwarded to admin.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "notifyAdmin");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                
                message.put("msg", "Notify admin.");
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                break;
            case "stopSession":
                message=new JSONObject();
                message.put("msg", "Session ended.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "stopSession");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                message.put("type", "execute");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                break;
            case "startSession":
                message=new JSONObject();
                message.put("msg", "Session started.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "startSession");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                message.put("type", "execute");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                break;
        }
        
        JSONObject response = new JSONObject();
        response.put("msg", "Action performed with success.");
        return response;
        
    }
    
}
