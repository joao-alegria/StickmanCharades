package es_g54;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
//@BasicAuthenticationMechanismDefinition(realmName = "stickmanCharades")
//@ApplicationScoped
//@Named
public class StickmanCharades extends Application {
    
}
