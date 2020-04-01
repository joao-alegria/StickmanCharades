package es_g54.security;

import javax.security.enterprise.credential.BasicAuthenticationCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

public class AuthIdentityStore implements IdentityStore {
    
    public CredentialValidationResult validate(BasicAuthenticationCredential credential) {
        // TODO check against db data
        return null;
    }
    
}
