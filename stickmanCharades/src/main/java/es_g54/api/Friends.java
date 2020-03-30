package es_g54.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author joaoalegria
 */
@Path("/friends")
public class Friends {

    /**
     * TODO: maybe receive filtering parameters
     * @return 
     */
    @GET
    public Response getAllFriends(){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    public Response addNewFriend(){
        return Response
                .ok("ping")
                .build();
    }
    
    @DELETE
    @Path("/{frinedId}")
    public Response deleteFriend(@PathParam("friendId") Integer friendId){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    @Path("/{frinedId}/session/{sessionId}")
    public Response inviteFriendToSession(@PathParam("friendId") Integer friendId, @PathParam("sessionId") Integer sessionId){
        return Response
                .ok("ping")
                .build();
    }
    
}
