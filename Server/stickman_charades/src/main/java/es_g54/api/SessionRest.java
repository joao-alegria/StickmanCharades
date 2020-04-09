package es_g54.api;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class SessionRest {

    @GetMapping(value="/session")
    public Response getAllSessions(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @PostMapping(value="/session")
    public Response createNewSession(@Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @GetMapping(value="/session/{sessionId}")
    public Response getSessionInfo(@PathVariable Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    /**
     * other users session
     * @param sessionId
     * @return 
     */
    @PostMapping(value="/session/{sessionId}")
    public Response joinOrLeaveSession(@PathVariable Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    /**
     * own sessions
     * @param sessionId
     * @return 
     */
    @PutMapping(value="/session/{sessionId}")
    public Response updateOrStartSession(@PathVariable Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
    
    @DeleteMapping(value="/session/{sessionId")
    public Response deleteSession(@PathVariable Integer sessionId, @Context SecurityContext securityContext){
        return Response
                .ok("ping")
                .build();
    }
}
