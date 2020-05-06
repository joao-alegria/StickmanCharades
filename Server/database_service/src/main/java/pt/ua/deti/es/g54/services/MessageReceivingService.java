package pt.ua.deti.es.g54.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ua.deti.es.g54.entities.DBCommand;
import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.CommandRepository;
import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.repository.UserRepository;

/**
 *
 * @author joaoalegria
 */
@Service
public class MessageReceivingService {
    
    @Autowired
    private KafkaTemplate<String,String>  kt;
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private CommandRepository cr;
    
    private JSONParser jparser=new JSONParser();

    @KafkaListener(topics="esp54_databaseServiceTopic")
    private void receiveMessage(String command){
        try {
            JSONObject json=(JSONObject)jparser.parse(command);
            processMessage(json);
        } catch (ParseException ex) {
            Logger.getLogger(MessageReceivingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void processMessage(JSONObject json){
        switch((String)json.get("type")){
            case "update":
                if(((String)json.get("command")).equals("startSession")){
                    DBSession targetSession = null;
                    String[] processedSessionName = ((String)json.get("session")).split("_");
                    List<DBSession> listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                    }
                    targetSession.setIsActive(true);
                    sr.save(targetSession);
                }else if(((String)json.get("command")).equals("stopSession")){
                    DBSession targetSession = null;
                    String[] processedSessionName = ((String)json.get("session")).split("_");
                    List<DBSession> listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                    }
                    targetSession.setIsActive(false);
                    sr.save(targetSession);
                }
                break;
            case "command":
                DBUser commandCreator = null;
                List<DBUser> listUser=ur.getUserByUsername((String)json.get("username"));
                if(!listUser.isEmpty()){
                    commandCreator = listUser.get(0);
                }
                DBSession targetSession = null;
                String[] processedSessionName = ((String)json.get("session")).split("_");
                List<DBSession> listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                if(!listSessions.isEmpty()){
                    targetSession=listSessions.get(0);
                }
                DBCommand c = new DBCommand(commandCreator, targetSession, (String)json.get("msg"), (String)json.get("command"));
                cr.save(c);
                break;
        }
    }

}
