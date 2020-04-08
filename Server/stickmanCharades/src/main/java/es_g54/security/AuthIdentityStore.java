package es_g54.security;

import es_g54.entities.DBUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.security.enterprise.credential.BasicAuthenticationCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Arrays;
import java.util.logging.Logger;

public class AuthIdentityStore implements IdentityStore {

    @PersistenceUnit(unitName = "db")
    EntityManagerFactory emf;

    Logger logger = Logger.getLogger(AuthIdentityStore.class.getName());

    public CredentialValidationResult validate(BasicAuthenticationCredential credential) {
        EntityManager em = emf.createEntityManager();

        DBUser user;
        try {
            user = (DBUser) em
                    .createQuery("SELECT u FROM DBUser u WHERE u.username = :username")
                    .setParameter("username", credential.getCaller())
                    .getSingleResult();
        }
        catch (NoResultException ex) {
            return CredentialValidationResult.INVALID_RESULT;
        }
        finally {
            em.close();
        }

        logger.info("user retrieved on identity store");
        logger.info(Arrays.toString(user.getPassword()));
        logger.info(Arrays.toString(user.getSalt()));

        if (!PasswordHashing.verify(credential.getPassword().getValue(), user.getSalt(), user.getPassword())) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        return new CredentialValidationResult(user.getUsername(), user.getGroups());
    }
    
}
