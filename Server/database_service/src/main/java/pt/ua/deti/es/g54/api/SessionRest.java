package pt.ua.deti.es.g54.api;

import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.services.SessionService;
import java.security.Principal;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class SessionRest {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionRest.class);

    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private SessionService ss;

    @GetMapping(value="/session")
    public JSONObject getAllSessions(Principal principal,@RequestParam Optional<Boolean> available, @RequestParam Optional<Boolean> active){
        logger.info("Get all session for user " + principal.getName());

        boolean act=false;
        boolean ava=false;
        if(active.isPresent()){
            act=active.get();
        }
        if(available.isPresent()){
            ava=available.get();
        }
        
        return ss.getAllSessions(principal.getName(), ava, act);
    }
    
    @PostMapping(value="/session")
    public JSONObject createNewSession(@RequestBody JSONObject newSession, Principal principal){
        logger.info("Create session request by user " + principal.getName());

        return ss.createNewSession(principal.getName(), newSession);
    }
    
    @GetMapping(value="/session/{sessionId}")
    public JSONObject getSessionInfo(@PathVariable Long sessionId, Principal principal){
        logger.info(String.format(
            "Get session %d info requested by user %s",
            sessionId,
            principal.getName()
        ));

        return ss.getSessionInfo(principal.getName(), sessionId);
    }
    
    /**
     * other users session
     * @param sessionId
     * @return 
     */
    @PostMapping(value="/session/{sessionId}")
    public JSONObject joinOrLeaveSession(@PathVariable Long sessionId, Principal principal, @RequestBody JSONObject action){
        logger.info(String.format(
            "Join/Leave request for session %d by user %s",
            sessionId,
            principal.getName()
        ));

        return ss.joinOrLeaveSession(principal.getName(), sessionId, action);
    }
    
    /**
     * own sessions
     * @param sessionId
     * @return 
     */
//    @PutMapping(value="/session/{sessionId}")
//    public JSONObject updateOrStartSession(@PathVariable Long sessionId, Principal principal){
//        return ss.updateSession(principal.getName(), sessionId, new JSONObject());
//    }
    
    @DeleteMapping(value="/session/{sessionId}")
    public JSONObject deleteSession(@PathVariable Long sessionId, Principal principal){
        logger.info(String.format(
            "Delete requet for session %d by user %s",
            sessionId,
            principal.getName()
        ));

        return ss.deleteSession(principal.getName(), sessionId);
    }
    
}
