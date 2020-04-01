package es_g54.api;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author joaoalegria
 */
@Path("/session")
@DeclareRoles({"user"})
public class Session {
    
    /**
     * TODO: need to consider receiving parameters
     * @return 
     */
    @GET
    @RolesAllowed({"user"})
    public Response getAllSessions(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    @RolesAllowed({"user"})
    public Response createNewSession(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @GET
    @Path("/{sessionId}")
    @RolesAllowed({"user"})
    public Response getSessionInfo(@PathParam("sessionId") Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    /**
     * other users session
     * @param sessionId
     * @return 
     */
    @POST
    @Path("/{sessionId}")
    @RolesAllowed({"user"})
    public Response joinOrLeaveSession(@PathParam("sessionId") Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    /**
     * own sessions
     * @param sessionId
     * @return 
     */
    @PUT
    @Path("/{sessionId}")
    @RolesAllowed({"user"})
    public Response updateOrStartSession(@PathParam("sessionId") Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @DELETE
    @Path("/{sessionId}")
    @RolesAllowed({"user"})
    public Response deleteSession(@PathParam("sessionId") Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
}
