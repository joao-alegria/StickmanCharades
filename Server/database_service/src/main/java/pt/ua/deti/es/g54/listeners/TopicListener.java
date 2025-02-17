package pt.ua.deti.es.g54.listeners;


import io.micrometer.core.instrument.Counter;
import io.micrometer.statsd.StatsdMeterRegistry;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ua.deti.es.g54.Constants;
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
@EnableKafka
public class TopicListener {

    private static Logger logger = LoggerFactory.getLogger(TopicListener.class);
    
    private JSONParser jparser=new JSONParser();
    
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

    @Autowired
    private StatsdMeterRegistry meterRegistry;
    
    private boolean countersCreated = false;

    private Counter stopSession;

    private Counter startSession;

    private Counter wordGuess;

    private Counter initialPosition;

    @KafkaListener(topics= Constants.LISTENING_TOPIC)
    private void receiveMessage(String command){
        logger.info("Record received on listening topic");

        try {
            JSONObject json=(JSONObject)jparser.parse(command);
            processMessage(json);
        } catch (ParseException ex) {
            logger.error("Error while converting received record to json", ex);
        }
    }
    
    public void processMessage(JSONObject json){
        synchronized (meterRegistry) {
            if (!countersCreated) {
                stopSession = Counter.builder("esp54_stopSession").register(meterRegistry);
                startSession = Counter.builder("esp54_startSession").register(meterRegistry);
                wordGuess = Counter.builder("esp54_wordGuess").register(meterRegistry);
                initialPosition = Counter.builder("esp54_initialPosition").register(meterRegistry);

                countersCreated = true;
            }
        }
        
        List<DBUser> listUser;
        DBSession targetSession;
        String[] processedSessionName;
        List<DBSession> listSessions;
        switch((String)json.get("type")){
            case "execute":
                if(((String)json.get("command")).equals("startSession")){
                    synchronized (startSession) {
                        startSession.increment();
                    }
                    
                    logger.info("Execute command start session received for session " + json.get("session"));
                    targetSession = null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                        targetSession.setIsActive(true);
                        ge.startGame((String)json.get("session"), targetSession );
                        sr.save(targetSession);
                    }else{
                        logger.error("Execute startSession command failed. No session session with name" + json.get("session"));
                    }
                }else if(((String)json.get("command")).equals("stopSession")){
                    synchronized (stopSession) {
                        stopSession.increment();
                    }
                    
                    logger.info("Execute command stop session received for session " + json.get("session"));
                    targetSession = null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                        targetSession.setIsActive(false);
                        targetSession.setIsAvailable(false);
                        ge.stopGame((String)json.get("session"));
                        sr.save(targetSession);
                    }else{
                        logger.error("Execute stopSession command failed. No session session with name" + json.get("session"));
                    }
                }else{
                    logger.error("Execute command failed. Unknown command " + json.get("command"));
                }
                break;
            case "command":
                logger.info(String.format(
                    "Command received for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));
                DBUser commandCreator = null;
                listUser=ur.getUserByUsername((String)json.get("username"));
                if(!listUser.isEmpty()){
                    commandCreator = listUser.get(0);
                }else{
                    logger.warn(String.format(
                        "No user found with username %s while processing received command",
                        json.get("username")
                    ));
                }
                targetSession = null;
                processedSessionName = ((String)json.get("session")).split("_");
                listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                if(!listSessions.isEmpty()){
                    targetSession=listSessions.get(0);
                }else{
                    logger.warn(String.format(
                        "No session found with name %s while processing received command",
                        json.get("session")
                    ));
                }
                DBCommand c = new DBCommand(commandCreator, targetSession, (String)json.get("msg"), (String)json.get("command"));
                cr.save(c);
                break;
            case "event":
                logger.info(String.format(
                    "Event received for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));
                DBUser eventCreator = null;
                listUser=ur.getUserByUsername((String)json.get("username"));
                if(!listUser.isEmpty()){
                    eventCreator = listUser.get(0);
                }else{
                    logger.warn(String.format(
                        "No user found with username %s while processing received event",
                        json.get("username")
                    ));
                }
                targetSession = null;
                processedSessionName = ((String)json.get("session")).split("_");
                listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                if(!listSessions.isEmpty()){
                    targetSession=listSessions.get(0);
                }else{
                    logger.warn(String.format(
                        "No session found with name %s while processing received event",
                        json.get("session")
                    ));
                }
                DBEvent e = new DBEvent(eventCreator, targetSession, (String)json.get("event"), (Long)json.get("time"));
                er.save(e);
                if(((String)json.get("event")).equals("initialPosition")){
                    synchronized (initialPosition) {
                        initialPosition.increment();
                    }
                    
                    ge.registerPlayers((String)json.get("session"), targetSession, (String)json.get("username"));
                }
                break;
            case "wordGuess":
                synchronized (wordGuess) {
                    wordGuess.increment();
                }
                    
                logger.info(String.format(
                    "Word guess received for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));

                json.remove("type");
                boolean continueGame=ge.processGuess(json);
                if(!continueGame){
                    targetSession=null;
                    processedSessionName = ((String)json.get("session")).split("_");
                    listSessions = sr.getSessionById(Long.valueOf(processedSessionName[1]));
                    if(!listSessions.isEmpty()){
                        targetSession=listSessions.get(0);
                        targetSession.setIsActive(false);
                        sr.save(targetSession);
                    }
                    else {
                        logger.error("Word guess failed because no session was found with the name " + json.get("session"));
                    }
                }
                break;
        }
    }

}
