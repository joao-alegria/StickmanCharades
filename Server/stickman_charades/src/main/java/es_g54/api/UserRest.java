package es_g54.api;

import es_g54.entities.DBGroup;
import es_g54.entities.DBUser;
import es_g54.repository.UserRepository;
import es_g54.utils.PasswordHashing;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class UserRest {
    
    private Logger logger = LoggerFactory.getLogger(UserRest.class.getName());
            
    @Autowired
    private UserRepository ur;

    @GetMapping(value="/login")
    public Response login(){
        //ks.sendMessage();
        return Response
                .ok("Login successful")
                .build();
    }

    @PostMapping(value="/register")
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

        DBUser user = new DBUser("aspedrosa", "aspedrosa@ua.pt", salt, hashedPassword);

//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();

        DBGroup userGroup;

//        try {
            userGroup=ur.getGroup();
//        } catch (NoResults ex) {
//            userGroup = new DBGroup("user");
//        }

        user.addGroup(userGroup);

//        em.persist(user);
//        em.getTransaction().commit();
//        em.close();
        ur.save(user);

        //Arrays.fill(hashedPassword, Byte.MIN_VALUE);
        //Arrays.fill(salt, Byte.MIN_VALUE);

        //ks.sendMessage();

        return Response
                .ok("Registration successful")
                .build();
    }

    @GetMapping(value="/logout")
    public Response logout(){
        //ks.sendMessage();
        return Response
                .ok("Logout successful")
                .build();
    }
}
