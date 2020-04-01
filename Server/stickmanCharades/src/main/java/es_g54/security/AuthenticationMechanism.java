package es_g54.security;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationMechanism implements HttpAuthenticationMechanism {
    
    @Inject
    private AuthIdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            if (request.getPathInfo().equals("/login")) {
                CredentialValidationResult credValResult = null; //identityStore.validate(credential); TODO get credentials from the request
                if (credValResult.getStatus() == CredentialValidationResult.Status.VALID) {
                    request.getSession();
                    session.setAttribute("principal", credValResult.getCallerPrincipal());
                    session.setAttribute("groups", credValResult.getCallerGroups());
                    
                    return httpMessageContext.notifyContainerAboutLogin(
                        credValResult.getCallerPrincipal(),
                        credValResult.getCallerGroups()
                    );
                }
            }
            else if (request.getPathInfo().equals("/register")) {
                return httpMessageContext.notifyContainerAboutLogin(
                        "a",
                        new HashSet<>(Arrays.asList())
                    );
            }
        }
        else {
            return httpMessageContext.notifyContainerAboutLogin(
                (Principal) session.getAttribute("principal"),
                (Set<String>) session.getAttribute("groups")
            );
        }
        
        return httpMessageContext.responseUnauthorized();
    }
    
}
