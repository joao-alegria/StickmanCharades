package pt.ua.deti.es.g54.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import pt.ua.deti.es.g54.entities.DBRole;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
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

    private final Matcher emailMatcher = Pattern.compile("\\A[a-z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_‘{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z").matcher("");

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
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
            if (!emailMatcher.reset(adminEmail).matches() || adminEmail.length() > 25) {
                malformedFields.add("email (must be a valid email with 25 or less characters)");
            }
            if (adminPassword.length < 8 || adminPassword.length > 20) {
                malformedFields.add("password (length must be between 8 and 20)");
            }
            String errorMessage = buildErrorMsg(malformedFields, "Invalid field(s) ");
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

}
