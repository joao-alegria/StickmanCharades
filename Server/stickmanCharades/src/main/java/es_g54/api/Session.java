package es_g54.api;

import javax.annotation.security.DeclareRoles;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
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
@Path("/session")
//@DeclareRoles({ "foo", "bar", "kaz" })
//@ServletSecurity(@HttpConstraint(rolesAllowed = "foo"))
public class Session {
    
    /**
     * TODO: need to consider receiving parameters
     * @return 
     */
    @GET
    public Response getAllSessions(){
        return Response
                .ok("ping")
                .build();
    }
    
    @POST
    public Response createNewSession(){
        return Response
                .ok("ping")
                .build();
    }
    
    @GET
    @Path("/{sessionId}")
    public Response getSessionInfo(@PathParam("sessionId") Integer sessionId){
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
    public Response joinOrLeaveSession(@PathParam("sessionId") Integer sessionId){
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
    public Response updateOrStartSession(@PathParam("sessionId") Integer sessionId){
        return Response
                .ok("ping")
                .build();
    }
    
    @DELETE
    @Path("/{sessionId}")
    public Response deleteSession(@PathParam("sessionId") Integer sessionId){
        return Response
                .ok("ping")
                .build();
    }
    
}
