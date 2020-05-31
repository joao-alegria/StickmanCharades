package pt.ua.deti.es.g54.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import pt.ua.deti.es.g54.api.UserRest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

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

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (args.length == 0 || !args[0].equals("--config-admin")) {
                return;
            }

            Scanner sc = new Scanner(System.in);

            System.out.print("Admin password (adminadmin): ");
            String password = sc.nextLine();
            if (password.equals("")) {
                password = "adminadmin";
            }

            char[] adminPassword = password.toCharArray();

            String adminEmail = "";
            while (adminEmail.equals("")) {
                System.out.print("Admin email: ");
                adminEmail = sc.nextLine();
            }

            if (ur.adminUsersCounts() > 0) {
                System.err.println("There is already a admin user created.");
                System.exit(2);
                return;
            }

            List<String> malformedFields = new ArrayList<>();
            if (!Pattern.compile(UserRest.emailPattern).matcher(adminEmail).matches() || adminEmail.length() > 25) {
                malformedFields.add("email (must be a valid email with 25 or less characters)");
            }
            if (adminPassword.length < 8 || adminPassword.length > 20) {
                malformedFields.add("password (length must be between 8 and 20)");
            }
            String errorMessage = UserRest.buildErrorMsg(malformedFields, "Invalid field(s) ");
            if (errorMessage != null) {
                System.err.println("Admin registration failed. " + errorMessage);
                System.exit(2);
            }

            if (ur.getEmailCount(adminEmail) > 0) {
                System.err.println("Admin registration failed. Fields already in use: email");
                System.exit(2);
            }

            DBUser admin = new DBUser(adminEmail, passwordEncoder.encode(CharBuffer.wrap(adminPassword)));

            List<DBRole> listAdminGroup = ur.getAdminRole();
            DBRole adminRole;

            if (listAdminGroup.isEmpty()) {
                adminRole = new DBRole("ROLE_ADMIN");
                rr.save(adminRole);
            }
            else {
                adminRole = listAdminGroup.get(0);
            }

            admin.addRole(adminRole);
            adminRole.addUser(admin);

            ur.save(admin);
            rr.save(adminRole);

            Arrays.fill(adminPassword, Character.MIN_VALUE);

            logger.info("Admin registered");

            System.exit(0);
        };
    }

}
