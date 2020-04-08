package es_g54.api;

import es_g54.api.entities.UserData;
import es_g54.entities.DBRole;
import es_g54.kafka.KafkaService;
import es_g54.entities.DBUser;
import es_g54.security.PasswordHashing;
import es_g54.services.UserService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 
 */

@Path("/")
public class UserControl {

    Logger logger = Logger.getLogger(UserControl.class.getName());

    @Inject
    KafkaService ks;

    @Inject
    UserService userService;

    Matcher emailMatcher = Pattern.compile( // https://regular-expressions.mobi/email.html?wlr=1
            "\\A[a-z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_‘{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z").matcher("");

    @GET
    @Path("/login")
    public Response login(){
        //ks.sendMessage();
        return Response
                .ok("Login successful")
                .build();
    }

    private String buildErrorMsg(List<String> malformedFields, String prefix) {
        if (!malformedFields.isEmpty()) {
            String missingFieldsOutput;
            if (malformedFields.size() > 1) {
                StringBuilder missingFieldsOutputBuilder = new StringBuilder();
                for (int i = 0; i < malformedFields.size(); i++) {
                    missingFieldsOutputBuilder.append(malformedFields.get(i));
                    if (i != malformedFields.size() - 1) {
                        missingFieldsOutputBuilder.append(", ");
                    }
                }
                missingFieldsOutput = missingFieldsOutputBuilder.toString();
            }
            else {
                missingFieldsOutput = malformedFields.get(0);
            }

            return prefix + missingFieldsOutput;
        }

        return null;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(UserData userData){
        if (userData == null) {
            return Response.status(400).entity("No user data provided").build();
        }

        List<String> malformedFields = new ArrayList<>();
        if (userData.getUsername() == null || userData.getUsername().length() == 0) {
            malformedFields.add("username");
        }
        else if (userData.getEmail() == null || userData.getEmail().length() == 0) {
            malformedFields.add("email");
        }
        else if (userData.getPassword() == null || userData.getPassword().length() == 0) {
            malformedFields.add("password");
        }

        String errorMessage = buildErrorMsg(malformedFields, "Empty field(s) ");
        if (errorMessage != null) {
            return Response.status(400).entity(errorMessage).build();
        }

        malformedFields.clear();
        if (userData.getUsername().length() < 3 || userData.getUsername().length() > 25) {
            malformedFields.add("username (length must be between 3 and 25)");
        }
        if (!emailMatcher.reset(userData.getEmail()).matches() || userData.getUsername().length() > 25) {
            malformedFields.add("email (must be a valid email with 25 or less characters)");
        }
        if (userData.getPassword().length() < 8 || userData.getPassword().length() > 20) {
            malformedFields.add("password (length must be between 8 and 20)");
        }

        errorMessage = buildErrorMsg(malformedFields, "Invalid field(s) ");
        if (errorMessage != null) {
            return Response.status(400).entity(errorMessage).build();
        }

        return userService.registerUser(userData);
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
