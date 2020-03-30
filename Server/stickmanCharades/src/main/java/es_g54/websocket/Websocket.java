package es_g54.websocket;

import es_g54.utils.JSONTextDecoder;
import es_g54.utils.JSONTextEncoder;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonObject;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

@ServerEndpoint(value = "/skeletons",
        decoders = { JSONTextDecoder.class }, 
        encoders = { JSONTextEncoder.class})
public class Websocket {
    
    private static final Logger logger = LoggerFactory.getLogger("Websocket");

    private static Set<Session> sessions = new HashSet<>();
 
    public void broadcastMessage(JsonObject message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }
 
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connection opened.");
        sessions.add(session);
    }
 
    @OnMessage
    public void onMessage(final Session session, JsonObject msg) throws IOException, EncodeException {
        logger.info("Received: "+msg.toString());
        
        //TODO
        
        //simply sending back message
        session.getBasicRemote().sendObject(msg);
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("WebSocket error for " + session.getId() + " " + throwable.getMessage());
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("WebSocket closed for " + session.getId() 
                            + " with reason " + closeReason.getCloseCode());
        sessions.remove(session);
    }
}

