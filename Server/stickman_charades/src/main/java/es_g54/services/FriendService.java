package es_g54.services;

import es_g54.entities.DBUser;
import es_g54.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class FriendService {
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public JSONObject getAllFriends(String username) {
        JSONObject jo = new JSONObject();
        DBUser user = ur.getUserByUsername(username);
        Set<String> friendNames = new HashSet();
        for(DBUser f : user.getFriends()){
            friendNames.add(f.getUsername());
        }
        jo.put("friends", ur);
        return jo;
    }
    
    public JSONObject addNewFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
        DBUser user = ur.getUserByUsername(username);
        DBUser friend = ur.getUserByUsername(friendname);
        if(!user.getFriends().contains(friend)){
            user.addFriend(friend);
        }
        return jo;
    }
    
    public JSONObject deleteFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
        DBUser user = ur.getUserByUsername(username);
        DBUser friend = ur.getUserByUsername(friendname);
        if(user.getFriends().contains(friend)){
            user.removeFriend(friend);
        }
        return jo;
    }
    
    public JSONObject inviteFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
//        DBUser user = ur.getUserByUsername(username);
//        DBUser friend = ur.getUserByUsername(friendname);
//        if(user.getFriends().contains(friend)){
////            producer.send(new String[]{"topic"}, 0, username);
//        }
        kafkaTemplate.send("mytopic", "banana");
        jo.put("key", "value");
        return jo;
    }

}
