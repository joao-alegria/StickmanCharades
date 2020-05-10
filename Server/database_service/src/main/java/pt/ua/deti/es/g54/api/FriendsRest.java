package pt.ua.deti.es.g54.api;

import pt.ua.deti.es.g54.services.FriendService;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class FriendsRest {

    private static final Logger logger = LoggerFactory.getLogger(FriendsRest.class);
    
    @Autowired
    private FriendService fs;
    
//    @Autowired
//    private ConsumerFactory consumerFactory;
    
    private KafkaConsumer consumer;

    @GetMapping(value = "/friends")
    public JSONObject getAllFriends(Principal principal) {
        logger.info("Request for all friends of user " + principal.getName());

        return fs.getAllFriends(principal.getName());
    }
    
    @GetMapping(value = "/friends/{friendname}")
    public JSONObject addNewFriend(@PathVariable String friendname,Principal principal){
        logger.info(String.format(
            "Add friend request for user %s by user %s",
            friendname,
            principal.getName()
        ));

        return fs.addNewFriend(principal.getName(), friendname);
    }
    
    @DeleteMapping(value="/friends/{friendname}")
    public JSONObject deleteFriend(@PathVariable String friendname,Principal principal){
        logger.info(String.format(
            "Delete friend request for user %s by user %s",
            friendname,
            principal.getName()
        ));

        return fs.deleteFriend(principal.getName(), friendname);
    }
    
    @GetMapping(value="/friends/{friendname}/session/{sessionId}")
    public JSONObject inviteFriendToSession(@PathVariable String friendname, @PathVariable Long sessionId,Principal principal){
        logger.info(String.format(
            "Invite friend to session request for user %s, for session %d by user %s",
            friendname,
            sessionId,
            principal.getName()
        ));

        return fs.inviteFriend(friendname, friendname, sessionId);
    }
    
}
