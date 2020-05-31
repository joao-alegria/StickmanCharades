package pt.ua.deti.es.g54.api;

import java.security.Principal;
import pt.ua.deti.es.g54.api.entities.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

import pt.ua.deti.es.g54.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.es.g54.entities.DBUser;

/**
 *
 * @author joaoalegria
 */
@RestController
@CrossOrigin(origins = "*")
public class UserRest {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRest.class);

    /**
     * // https://regular-expressions.mobi/email.html?wlr=1
     */
    public static final String emailPattern = "\\A[a-z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_‘{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z";
    private final Matcher emailMatcher = Pattern.compile(emailPattern).matcher("");

    @Autowired
    UserService userService;

    @GetMapping(value="/login")
    public ResponseEntity<String> login(Principal principal){
        JSONObject json = new JSONObject();
        json.put("msg", "Login Successful");
        DBUser user = userService.getUser(principal.getName());

        json.put("admin", user.isAdmin());
        logger.info("Login Successful");

        return ResponseEntity.ok(json.toJSONString());
    }

    public static String buildErrorMsg(List<String> malformedFields, String prefix) {
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

    @PostMapping(value="/register")
    public ResponseEntity<String> register(@RequestBody UserData userData){
        logger.info("Register POST");

        if (userData == null) {
            logger.error("User registration failed. No user data provided");
            return ResponseEntity.status(400).body("No user data provided");
        }

        List<String> malformedFields = new ArrayList<>();
        if (userData.getUsername() == null || userData.getUsername().length() == 0) {
            malformedFields.add("username");
        }
        else if (userData.getEmail() == null || userData.getEmail().length() == 0) {
            malformedFields.add("email");
        }
        else if (userData.getPassword() == null || userData.getPassword().length == 0) {
            malformedFields.add("password");
        }

        String errorMessage = buildErrorMsg(malformedFields, "Empty field(s) ");
        if (errorMessage != null) {
            logger.error("User registration failed. " + errorMessage);

            return ResponseEntity.status(400).body(errorMessage);
        }

        malformedFields.clear();
        if (userData.getUsername().length() < 3 || userData.getUsername().length() > 25) {
            malformedFields.add("username (length must be between 3 and 25)");
        }
        if (!emailMatcher.reset(userData.getEmail()).matches() || userData.getUsername().length() > 25) {
            malformedFields.add("email (must be a valid email with 25 or less characters)");
        }
        if (userData.getPassword().length < 8 || userData.getPassword().length > 20) {
            malformedFields.add("password (length must be between 8 and 20)");
        }

        errorMessage = buildErrorMsg(malformedFields, "Invalid field(s) ");
        if (errorMessage != null) {
            logger.error("User registration failed. " + errorMessage);
            return ResponseEntity.status(400).body(errorMessage);
        }

        return userService.registerUser(userData);
    }

    @GetMapping(value="/logout")
    public ResponseEntity<String> logout(Principal principal){
        logger.info("Logout successful");

        return ResponseEntity.ok("Logout successful");
    }
}
