package es_g54.api;

import es_g54.kafka.KafkaService;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author 
 */

@Path("/")
public class UserControl {
    
    @Inject
    KafkaService ks;
    
    @GET
    @Path("/login")
    public Response login(){
        ks.sendMessage();
        return Response
                .ok("ping")
                .build();
    }
    
    
    @POST
    @Path("/register")
    public Response register(){
        ks.sendMessage();
        return Response
                .ok("ping")
                .build();
    }
}
