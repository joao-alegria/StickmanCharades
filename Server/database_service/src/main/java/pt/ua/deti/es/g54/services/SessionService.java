package pt.ua.deti.es.g54.services;

import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    
    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private SimpMessagingTemplate smt;
    
    @Autowired
    private KafkaTemplate<String,String>  kt;

    public JSONObject getAllSessions(String name) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getAllSessions(name);
        Map<Long, Map<String, String>> sessions = new HashMap();
        if(!listSessions.isEmpty()){
            for(DBSession s : listSessions){
                if(s.getCreator().getUsername().equals(name)){
                    Map<String, String> details = new HashMap();
                    details.put("creator", s.getCreator().getUsername());
                    details.put("title", s.getTitle());
                    sessions.put(s.getId(), details);
                }
            }
        }
        jo.put("sessions", sessions);
        return jo;
    }

    public JSONObject createNewSession(String name, JSONObject newSession) {
        String title = (String)newSession.get("title");
        int duration = (int)newSession.get("duration");
        String[] words = (String[])((JSONArray)newSession.get("words")).toArray();
        List<DBUser> listUser=ur.getUserByUsername(name);
        JSONObject json =new JSONObject();
        if(!listUser.isEmpty()){
            DBUser user = listUser.get(0);
            DBSession session = new DBSession(title,duration,user,words);
            sr.save(session);
            json.put("id", session.getId());
        }
        else {
            logger.error("Create session request failed. No uer with username " + name);
        }
        return json;
    }

    public JSONObject getSessionInfo(String name, Long sessionId) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        DBSession session = null;
        if(!listSessions.isEmpty()){
            session=listSessions.get(0);
        }
        else {
            logger.error("Get session info request failed. No session with id " + sessionId);
        }
        jo.put("sessions", session);
        return jo;
    }

    public JSONObject joinOrLeaveSession(String name, Long sessionId, JSONObject response) {
        JSONObject jo = new JSONObject();
        String action = (String)response.get("action");
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        List<DBUser> listUser=ur.getUserByUsername(name);
        if(!listSessions.isEmpty() && !listUser.isEmpty()){
            DBUser user = listUser.get(0);
            DBSession session=listSessions.get(0);
            switch(action){
                case "join":
                    if(!session.getPlayers().contains(user)){
                        logger.info(String.format(
                            "User %s joined session %d",
                            name,
                            sessionId
                        ));
                        user.setSessionInPlay(session);
                        session.addPlayer(user);
                        ur.save(user);
                        sr.save(session);
                    }
                    else {
                        logger.error(String.format(
                            "Join request failed. User %s already on session %d",
                            name,
                            sessionId
                        ));
                    }
                    break;
                case "leave":
                    if(session.getPlayers().contains(user)){
                        logger.info(String.format(
                            "User %s left session %d",
                            name,
                            sessionId
                        ));
                        user.setSessionInPlay(null);
                        session.removePlayer(user);
                        ur.save(user);
                        sr.save(session);
                    }
                    else {
                        logger.error(String.format(
                            "Leave request failed. User %s in not on session %d",
                            name,
                            sessionId
                        ));
                    }
                    break;
                default:
                    logger.error(String.format(
                        "Join/Leave request for user %s for session %d failed. Invalid action \"%a\"",
                        action
                    ));
            }
        }
        
        return jo;
    }

    public JSONObject updateSession(String name, Long sessionId, JSONObject jsonObject) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        if(!listSessions.isEmpty()){
            DBSession session=listSessions.get(0);
//            Thread c = new Thread(new SessionConsumer(session,"esp54_"+String.valueOf(sessionId),smt, sr, kt));
//            c.start();

            JSONObject message = new JSONObject();
            message.put("", name);

            kt.send("esp54_eventHandlerTopic", message.toJSONString());
            kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
        }
        else {
            logger.error("Update session request failed. No session with id " + sessionId);
        }
        return jo;
    }

    public JSONObject deleteSession(String name, Long sessionId) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        if(!listSessions.isEmpty()){
            DBSession session=listSessions.get(0);
            session.setIsAvailable(false);
            sr.save(session);
        }
        else {
            logger.error("Delete session request failed. No session with id " + sessionId);
        }
        return jo;
    }

}
