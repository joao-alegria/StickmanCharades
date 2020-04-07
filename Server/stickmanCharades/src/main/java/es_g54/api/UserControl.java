package es_g54.api;

import es_g54.entities.DBGroup;
import es_g54.kafka.KafkaService;
import es_g54.entities.DBUser;
import es_g54.security.PasswordHashing;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author 
 */

@Path("/")
public class UserControl {

    Logger logger = Logger.getLogger(UserControl.class.getName());

    @Inject
    KafkaService ks;

    @PersistenceUnit(unitName = "db")
    EntityManagerFactory emf;

    @GET
    @Path("/login")
    public Response login(){
        //ks.sendMessage();
        return Response
                .ok("Login successful")
                .build();
    }

    @POST
    @Path("/register")
    public Response register(){

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hashedPassword;
        try {
            hashedPassword = PasswordHashing.hash(new char[] {'o', 'l', 'a'}, salt);
        } catch (InvalidKeySpecException e) {
            return Response.serverError().build();
        }

        logger.info(Arrays.toString(salt));

        DBUser user = new DBUser("aspedrosa", "aspedrosa@ua.pt", hashedPassword, salt);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        DBGroup userGroup;
        try {
            userGroup = (DBGroup) em
                    .createQuery("SELECT g from DBGroup g WHERE g.name = 'user'").getSingleResult();
        }
        catch (NoResultException ex) {
            userGroup = new DBGroup("user");
        }

        user.addGroup(userGroup);

        em.persist(user);
        em.getTransaction().commit();
        em.close();

        //Arrays.fill(hashedPassword, Byte.MIN_VALUE);
        //Arrays.fill(salt, Byte.MIN_VALUE);

        //ks.sendMessage();

        return Response
                .ok("Registration successful")
                .build();
    }

    @GET
    @Path("/logout")
    public Response logout(){
        //ks.sendMessage();
        return Response
                .ok("Logout successful")
                .build();
    }

}
