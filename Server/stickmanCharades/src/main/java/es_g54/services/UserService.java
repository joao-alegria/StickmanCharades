package es_g54.services;

import es_g54.api.entities.UserData;
import es_g54.entities.DBRole;
import es_g54.entities.DBUser;
import es_g54.security.PasswordHashing;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class UserService {

    Logger logger = Logger.getLogger(UserService.class.getName());

    @PersistenceUnit(unitName = "db")
    EntityManagerFactory emf;

    SecureRandom saltGenerator = new SecureRandom();

    public Response registerUser(UserData validatedUserData) {
        EntityManager em = emf.createEntityManager();

        long userCount = (long) em
                .createQuery("SELECT COUNT(u) FROM DBUser u WHERE u.username = :username")
                .setParameter("username", validatedUserData.getUsername())
                .getSingleResult();

        StringBuilder duplicatedFields = new StringBuilder();
        if (userCount > 0) {
            duplicatedFields.append("username");
        }

        userCount = (long) em
                .createQuery("SELECT COUNT(u) FROM DBUser u WHERE u.email = :email")
                .setParameter("email", validatedUserData.getEmail())
                .getSingleResult();
        if (userCount > 0) {
            if (duplicatedFields.length() > 0) {
                duplicatedFields.append(" and ");
            }
            duplicatedFields.append("email");
        }

        if (duplicatedFields.length() > 0) {
            return Response.status(400)
                    .entity(
                            String.format("Field(s) %s already in use", duplicatedFields.toString())
                    ).build();
        }

        byte[] salt = new byte[16];
        saltGenerator.nextBytes(salt);

        byte[] hashedPassword;
        try {
            hashedPassword = PasswordHashing.hash(validatedUserData.getPassword().toCharArray(), salt);
        } catch (InvalidKeySpecException e) {
            logger.severe("Unable to hash password");

            return Response.serverError().build();
        }

        DBUser user = new DBUser(validatedUserData, salt, hashedPassword);

        DBRole userRole;
        try {
            userRole = (DBRole) em
                    .createQuery("SELECT g from DBRole g WHERE g.name = 'user'").getSingleResult();
        }
        catch (NoResultException ex) {
            userRole = new DBRole("user");
        }

        user.addGroup(userRole);
        userRole.addUser(user);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();

        //Arrays.fill(hashedPassword, Byte.MIN_VALUE); // TODO uncomment and make it work
        //Arrays.fill(salt, Byte.MIN_VALUE);

        //ks.sendMessage();

        return Response
                .ok("Registration successful")
                .build();
    }
}
