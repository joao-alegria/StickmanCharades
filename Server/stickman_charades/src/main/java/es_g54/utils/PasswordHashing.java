package es_g54.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author joaoalegria
 */
public class PasswordHashing {

    private PasswordHashing() {}

    private final static int ITERATIONS = 65536;
    private final static int KEY_LENGTH = 512;
    private final static String ALGORITHM = "PBKDF2WithHmacSHA512";

    private final static Logger logger = Logger.getLogger(PasswordHashing.class.getName());

    public static byte[] hash(char[] password, byte[] salt) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);

        SecretKeyFactory fac = null;
        try {
            fac = SecretKeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // never happens
        }

        try {
            return fac.generateSecret(spec).getEncoded();
        }
        finally {
            spec.clearPassword();
        }
    }

    public static boolean verify(char[] password, byte[] salt, byte[] hashedPassword) {
        byte[] probableHashedPassword;

        try {
            probableHashedPassword = hash(password, salt);
        } catch (InvalidKeySpecException e) {
            logger.severe("Unable to hash password.");
            return false;
        }

        return Arrays.equals(probableHashedPassword, hashedPassword);
    }

}