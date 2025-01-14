package pt.ua.deti.es.g54.services;

import pt.ua.deti.es.g54.entities.DBFriendInvite;
import pt.ua.deti.es.g54.entities.DBGameInvite;
import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.FriendInviteRepository;
import pt.ua.deti.es.g54.repository.SessionRepository;
import pt.ua.deti.es.g54.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ua.deti.es.g54.repository.GameInviteRepository;
import java.util.ArrayList;

/**
 *
 * @author joaoalegria
 */
@Service
public class FriendService {

    private static final Logger logger = LoggerFactory.getLogger(FriendService.class);
    
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
        else {
            logger.error("Get all friends request failed. No user with username " + username);
        }
        return jo;
    }
    
    public JSONObject addNewFriend(String username, String friendname){
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        List<DBUser> listFriend = ur.getUserByUsername(friendname);
        if(!listUser.isEmpty() && !listFriend.isEmpty()){
            if(!listUser.get(0).getFriends().contains(listFriend.get(0))){
                List<DBFriendInvite> allInvites = fir.getInvitesNotAccepted();
                List<DBFriendInvite> listInvites = new ArrayList();
                for(DBFriendInvite tmp : allInvites){
                    if((tmp.geInviteCreator().getUsername().equals(username) && tmp.getInviteTarget().getUsername().equals(friendname)) || (tmp.geInviteCreator().getUsername().equals(friendname) && tmp.getInviteTarget().getUsername().equals(username))){
                        listInvites.add(tmp);
                    }
                }
                DBFriendInvite fi;
                if(listInvites.isEmpty()){
                    fi = new DBFriendInvite(listUser.get(0), listFriend.get(0));
                    kafkaTemplate.send(friendname, "{\"friendInvite\":{\"user\":\""+username+"\"}}");
                }else{
                    fi=listInvites.get(0);
                    fi.setAccepted(true);
                    fir.save(fi);
                    listFriend.get(0).addFriend(listUser.get(0));
                }
            }else{
                logger.warn(String.format(
                    "add friend request ignored. user %s is already fiend of %s",
                    friendname,
                    username
                ));
            }
        }else{
            logger.error(String.format(
                "Add friend request failed. No user with username %s or %s",
                username,
                friendname
            ));
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
            }else{
                logger.warn(String.format(
                    "Delete friend request ignored. user %s is not fiend of %s",
                    friendname,
                    username
                ));
            }
        }else{
            logger.error(String.format(
                "Delete friend request failed. No user with username %s or %s",
                username,
                friendname
            ));
        }
        return jo;
    }
    
    public JSONObject inviteFriend(String username, String friendname, Long sessionId){
        JSONObject jo = new JSONObject();
        List<DBUser> listUser = ur.getUserByUsername(username);
        List<DBUser> listFriend = ur.getUserByUsername(friendname);
        List<DBSession> listSession = sr.getSessionById(sessionId);
        if(!listUser.isEmpty() && !listFriend.isEmpty() && !listSession.isEmpty()){
            if(listUser.get(0).getFriends().contains(listFriend.get(0))){
                DBGameInvite invite = new DBGameInvite(listUser.get(0), listFriend.get(0), listSession.get(0));
                gir.save(invite);
                kafkaTemplate.send(friendname, "{\"gameInvite\":{\"friend\":"+username+", \"session\": "+sessionId+"}}");
            }else{
                logger.error(String.format(
                    "Delete friend request failed. user %s is not fiend of %s",
                    friendname,
                    username
                ));
            }
        }else{
            logger.error(String.format(
                "Delete friend request failed. No user with username %s or %s",
                username,
                friendname
            ));
        }
        return jo;
    }

}
