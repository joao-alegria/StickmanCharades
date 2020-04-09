package es_g54.services;

import es_g54.entities.DBFriendInvite;
import es_g54.entities.DBGameInvite;
import es_g54.entities.DBSession;
import es_g54.entities.DBUser;
import es_g54.repository.FriendInviteRepository;
import es_g54.repository.SessionRepository;
import es_g54.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import es_g54.repository.GameInviteRepository;

/**
 *
 * @author joaoalegria
 */
@Service
public class FriendService {
    
    @Autowired
    private UserRepository ur;
    
    @Autowired
    private SessionRepository sr;
    
    @Autowired
    private GameInviteRepository gir;
    
    @Autowired
    private FriendInviteRepository fir;
    
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public JSONObject getAllFriends(String username) {
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        Set<String> friendNames = new HashSet();
        if(!listUser.isEmpty()){
            for(DBUser f : listUser.get(0).getFriends()){
                friendNames.add(f.getUsername());
            }
            jo.put("friends", friendNames);
        }
        return jo;
    }
    
    public JSONObject addNewFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        List<DBUser> listFriend = ur.getUserByUsername(friendname);
        if(!listUser.isEmpty() && !listFriend.isEmpty()){
            if(!listUser.get(0).getFriends().contains(listFriend.get(0))){
                List<DBFriendInvite> listInvites = fir.getInviteByUsernames(username, friendname);
                DBFriendInvite fi;
                if(listInvites.isEmpty()){
                    fi = new DBFriendInvite(listUser.get(0), listFriend.get(0));
                    kafkaTemplate.send(friendname, "{\"friendInvite\":{\"user\":"+username+"}}");
                }else{
                    fi=listInvites.get(0);
                    fi.setAccepted(true);
                    fir.save(fi);
                    listFriend.get(0).addFriend(listUser.get(0));
                }
            }
        }
        return jo;
    }
    
    public JSONObject deleteFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        List<DBUser> listFriend = ur.getUserByUsername(friendname);
        if(!listUser.isEmpty() && !listFriend.isEmpty()){
            if(listUser.get(0).getFriends().contains(listFriend.get(0))){
                listUser.get(0).removeFriend(listFriend.get(0));
            }
        }
        return jo;
    }
    
    public JSONObject inviteFriend(String username, String friendname, Long sessionId){
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        List<DBUser> listFriend = ur.getUserByUsername(friendname);
        DBSession session = sr.getSessionById(sessionId);
        if(!listUser.isEmpty() && !listFriend.isEmpty()){
            if(listUser.get(0).getFriends().contains(listFriend.get(0))){
                DBGameInvite invite = new DBGameInvite(listUser.get(0), listFriend.get(0), session);
                gir.save(invite);
                kafkaTemplate.send(friendname, "{\"gameInvite\":{\"friend\":"+username+", \"session\": "+sessionId+"}}");
            }
        }
        jo.put("key", "value");
        return jo;
    }

}
