package pt.ua.deti.es.g54.services;

import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.repository.UserRepository;
import pt.ua.deti.es.g54.utils.SessionConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
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
    
    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private SimpMessagingTemplate smt;
    
    @Autowired
    private KafkaTemplate<String,String>  kt;
    
    @Value("${KAFKA_HOST}")
    private String KAFKA_HOST;

    @Value("${KAFKA_PORT}")
    private String KAFKA_PORT;

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
        List<DBUser> listUser=ur.getUserByUsername(name);
        if(!listUser.isEmpty()){
            DBUser user = listUser.get(0);
            DBSession session = new DBSession(title,duration,user);
            sr.save(session);
        }
        return new JSONObject();
    }

    public JSONObject getSessionInfo(String name, Long sessionId) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        DBSession session = null;
        if(!listSessions.isEmpty()){
            session=listSessions.get(0);
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
                        user.setSessionInPlay(session);
                        ur.save(user);
                    }
                    break;
                case "leave":
                    if(session.getPlayers().contains(user)){
                        user.setSessionInPlay(null);
                        ur.save(user);
                    }
                    break;
            }
        }
        
        return jo;
    }

    public JSONObject updateSession(String name, Long sessionId, JSONObject jsonObject) {
        JSONObject jo = new JSONObject();
        List<DBSession> listSessions = sr.getSessionById(sessionId);
        if(!listSessions.isEmpty()){
            DBSession session=listSessions.get(0);
            session.setIsActive(true);
//            Thread s = new Thread(new Session(session.getId(), session.getDurationSeconds(), session.getCreator()));
            Thread c = new Thread(new SessionConsumer(KAFKA_HOST, KAFKA_PORT,session,"esp54_"+String.valueOf(sessionId),smt, sr));
            c.start();
//            s.start();
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
        return jo;
    }

}
