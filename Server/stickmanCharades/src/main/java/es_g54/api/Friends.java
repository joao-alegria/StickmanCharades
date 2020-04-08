package es_g54.api;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author joaoalegria
 */
@Path("/friends")
@DeclareRoles({"user"})
public class Friends {

    /**
     * TODO: maybe receive filtering parameters
     * @return 
     */
    @GET
    @RolesAllowed({"user"})
    public Response getAllFriends(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    @RolesAllowed({"user"})
    public Response addNewFriend(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @DELETE
    @Path("/{frinedId}")
    @RolesAllowed({"user"})
    public Response deleteFriend(@PathParam("friendId") Integer friendId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    @Path("/{frinedId}/session/{sessionId}")
    @RolesAllowed({"user"})
    public Response inviteFriendToSession(@PathParam("friendId") Integer friendId, @PathParam("sessionId") Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
}
