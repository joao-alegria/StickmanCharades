package pt.ua.deti.es.g54.listeners;


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
import pt.ua.deti.es.g54.entities.DBEvent;
import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.CommandRepository;
import pt.ua.deti.es.g54.repository.EventRepository;
import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.repository.UserRepository;
import pt.ua.deti.es.g54.services.GameEngine;

/**
 *
 * @author joaoalegria
 */
@Service
public class TopicListener {
    
    @Autowired
    private KafkaTemplate<String,String>  kt;
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private CommandRepository cr;
    
    @Autowired
    private EventRepository er;
    
    @Autowired
    private GameEngine ge;
    
    private JSONParser jparser=new JSONParser();

    @KafkaListener(topics="esp54_databaseServiceTopic")
    private void receiveMessage(String command){
        try {
            JSONObject json=(JSONObject)jparser.parse(command);
            processMessage(json);
        } catch (ParseException ex) {
            Logger.getLogger(TopicListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void processMessage(JSONObject json){
        List<DBUser> listUser;
        DBSession targetSession;
        String[] processedSessionName;
        List<DBSession> listSessions;
        switch((String)json.get("type")){
            case "execute":
                if(((String)json.get("command")).equals("startSession")){
                    targetSession = null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                    }
                    targetSession.setIsActive(true);
                    ge.startGame((String)json.get("session"), targetSession);
                    sr.save(targetSession);
                }else if(((String)json.get("command")).equals("stopSession")){
                    targetSession = null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                    }
                    targetSession.setIsActive(false);
                    ge.stopGame((String)json.get("session"), targetSession);
                    sr.save(targetSession);
                }
                break;
            case "command":
                DBUser commandCreator = null;
                listUser=ur.getUserByUsername((String)json.get("username"));
                if(!listUser.isEmpty()){
                    commandCreator = listUser.get(0);
                }
                targetSession = null;
                processedSessionName = ((String)json.get("session")).split("_");
                listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                if(!listSessions.isEmpty()){
                    targetSession=listSessions.get(0);
                }
                DBCommand c = new DBCommand(commandCreator, targetSession, (String)json.get("msg"), (String)json.get("command"));
                cr.save(c);
                break;
            case "event":
                DBUser eventCreator = null;
                listUser=ur.getUserByUsername((String)json.get("username"));
                if(!listUser.isEmpty()){
                    commandCreator = listUser.get(0);
                }
                targetSession = null;
                processedSessionName = ((String)json.get("session")).split("_");
                listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                if(!listSessions.isEmpty()){
                    targetSession=listSessions.get(0);
                }
                DBEvent e = new DBEvent(eventCreator, targetSession, (String)json.get("event"), (Long)json.get("time"));
                er.save(e);
                break;
            case "wordGuess":
                json.remove("type");
                boolean continueGame=ge.processGuess(json);
                if(!continueGame){
                    targetSession=null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                    }
                    targetSession.setIsActive(false);
                    sr.save(targetSession);
                }
                break;
        }
    }

}
