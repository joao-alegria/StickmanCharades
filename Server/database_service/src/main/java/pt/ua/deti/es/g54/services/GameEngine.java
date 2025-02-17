/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ua.deti.es.g54.Constants;
import pt.ua.deti.es.g54.entities.DBSession;

/**
 *
 * @author joaoalegria
 */
@Service
public class GameEngine{

	private static Logger logger = LoggerFactory.getLogger(GameEngine.class);
    
    private List<OngoingGame> games = new ArrayList();
    
    private Map<DBSession, List<String>> players=new HashMap();
    
    @Autowired
    private KafkaTemplate<String,String>  kt;
    
    public void startGame(String sessionTopic, DBSession session){
        logger.info("Starting game. Listenning on topic " + sessionTopic);

        OngoingGame game = new OngoingGame(sessionTopic, session);
        games.add(game);
        JSONObject message = new JSONObject();
        message.put("username", game.getGuesser());
        message.put("word", game.getWord());
        message.put("type", "gameOrder");
        kt.send(sessionTopic, message.toJSONString());
    }
    
    public boolean processGuess(JSONObject json){
        String sessionTopic=(String)json.get("session");
        for(OngoingGame game:games){
            if(game.getSessionTopic().equals(sessionTopic)){
                if(game.assertGuess((String)json.get("username"),(String)json.get("guess"))){
                    logger.info(String.format(
                        "User %s made a corret guess (%s) on session %d",
                        json.get("username"),
                        json.get("guess"),
                        json.get("session")
                    ));
                    JSONObject message = new JSONObject();
                    message.put("type", "gameInfo");
                    message.put("info", "Round Ended.");
                    message.put("winner", (String)json.get("username"));
                    kt.send(sessionTopic, message.toJSONString());
                    if(game.continueGame()){
                        message = new JSONObject();
                        message.put("username", game.getGuesser());
                        message.put("word", game.getWord());
                        message.put("type", "gameOrder");
                        logger.info(String.format(
                            "Next word to guess on session %s: %s",
                            json.get("session"),
                            game.getWord()
                        ));
                        kt.send(sessionTopic, message.toJSONString());
                    }else{
                        message = new JSONObject();
                        message.put("type", "gameInfo");
                        message.put("info", "Game Ended.");
                        message.put("winner", game.getWinner());
                        kt.send(sessionTopic, message.toJSONString());
                        games.remove(game);
                        logger.info(String.format(
                            "Game of session %s finished. Winner: %s",
                            json.get("session"),
                            game.getWinner()
                        ));
                        return false;
                    }
                }else{
                    logger.info(String.format(
                        "User %s made an incorret guess (%s) on session %d",
                        json.get("username"),
                        json.get("guess"),
                        json.get("session")
                    ));
                }
                break;
            }
        }
        return true;
    }
    
    public void stopGame(String sessionTopic){
        logger.info("Stopping game associated with listenning topic " + sessionTopic);

        for(OngoingGame game:games){
            if(game.getSessionTopic().equals(sessionTopic)){
                games.remove(game);
                JSONObject message = new JSONObject();
                message.put("msg", "Session ended.");
                message.put("type", "gameOrder");
                kt.send(sessionTopic, message.toJSONString());
            }
        }
    }
    
    public void registerPlayers(String sessionTopic, DBSession session, String username){
        List<String> names;
        if(!players.keySet().contains(session)){
            names = new ArrayList();
            names.add(username);
            players.put(session, names);
        }
        names=players.get(session);
        
        JSONObject message = new JSONObject();
        message.put("type", "gameInfo");
        message.put("info", "User Recognized.");
        kt.send(sessionTopic, message.toJSONString());
        kt.flush();
        if(names.size()==session.getPlayers().size()){
//            JSONObject message = new JSONObject();
//            message.put("type", "gameInfo");
//            message.put("info", "Game Started.");
//            kt.send(sessionTopic, message.toJSONString());
//            this.startGame(sessionTopic, session);
            message = new JSONObject();
            message.put("command", "startSession");
            message.put("username", username);
            message.put("session", sessionTopic);
            kt.send(Constants.COMMANDS_SERVICE_TOPIC, message.toJSONString());
            kt.flush();
        }
    }
    
}
