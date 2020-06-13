package pt.ua.deti.es.g54.services;

import pt.ua.deti.es.g54.api.entities.UserData;
import pt.ua.deti.es.g54.entities.DBRole;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import pt.ua.deti.es.g54.repository.RoleRepository;

@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository ur;
    
    @Autowired
    private RoleRepository rr;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> registerUser(UserData validatedUserData) {
        StringBuilder duplicatedFields = new StringBuilder();
        if (ur.getUsernameCount(validatedUserData.getUsername()) > 0) {
            duplicatedFields.append("username");
        }

        if (ur.getEmailCount(validatedUserData.getEmail()) > 0) {
            if (duplicatedFields.length() > 0) {
                duplicatedFields.append(" and ");
            }
            duplicatedFields.append("email");
        }

        if (duplicatedFields.length() > 0) {
            logger.error("User registration failed. Fields already in use: " + duplicatedFields.toString());

            return ResponseEntity.status(400)
                    .body(
                            String.format("Field(s) %s value(s) already in use", duplicatedFields.toString())
                    );
        }

        DBUser user = new DBUser(
                validatedUserData,
                passwordEncoder.encode(CharBuffer.wrap(validatedUserData.getPassword()))
        );

        List<DBRole> listUserGroup = ur.getRole();
        DBRole userRole;

        if (listUserGroup.isEmpty()) {
            userRole = new DBRole("ROLE_USER");
            rr.save(userRole);
        } else {
            userRole = listUserGroup.get(0);
        }

        user.addRole(userRole);
        userRole.addUser(user);
        
        ur.save(user);
        rr.save(userRole);

        Arrays.fill(validatedUserData.getPassword(), Character.MIN_VALUE);

        logger.info("User registration successful. New user " + validatedUserData.getUsername());

        return ResponseEntity.ok("Registration successful");
    }

    public DBUser getUser(String username) {
        List<DBUser> users = ur.getUserByUsername(username);
        if(users.size()==1){
            return users.get(0);
        }
       return null;
    }

}
