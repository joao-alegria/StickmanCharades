package es_g54.api;

import es_g54.services.FriendService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private FriendService fs;

    @GetMapping(value = "/friends")
    public Response getAllFriends(@Context SecurityContext securityContext) {
        return Response.ok(fs.getAllFriends(securityContext.getUserPrincipal().getName())).build();
    }
    
    @PostMapping(value = "/friends")
    public Response addNewFriend(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @DeleteMapping(value="/friends/{friendname}")
    public Response deleteFriend(@PathVariable String friendname, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @GetMapping(value="/friends/{friendname}/session/{sessionId}")
    public JSONObject inviteFriendToSession(@PathVariable String friendname, @PathVariable Integer sessionId){
        return fs.inviteFriend(friendname, friendname);
    }
    
}
