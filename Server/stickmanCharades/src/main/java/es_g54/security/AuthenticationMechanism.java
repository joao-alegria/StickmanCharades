package es_g54.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Set;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.BasicAuthenticationCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationMechanism implements HttpAuthenticationMechanism {

    private Logger logger = Logger.getLogger(AuthenticationMechanism.class.getName());
    
    @Inject
    private AuthIdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        HttpSession session = request.getSession(false);

        if (session == null) {

            logger.info("no session");

            if (request.getPathInfo().equals("/login")) {

                logger.info("login");

                String authorizationHeader = request.getHeader("Authorization"); // this should be a character array
                if (authorizationHeader == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write("Authorization header missing.");
                    } catch (IOException e) {}

                    return AuthenticationStatus.SEND_FAILURE;
                }

                BasicAuthenticationCredential credentials = new BasicAuthenticationCredential(
                        authorizationHeader.substring(6)
                );
                CredentialValidationResult credValResult = identityStore.validate(credentials);
                credentials.clear();

                if (credValResult.getStatus() == CredentialValidationResult.Status.VALID) {
                    session = request.getSession();
                    session.setAttribute("principal", credValResult.getCallerPrincipal());
                    session.setAttribute("groups", credValResult.getCallerGroups());

                    return httpMessageContext.notifyContainerAboutLogin(
                        credValResult.getCallerPrincipal(),
                        credValResult.getCallerGroups()
                    );
                }
            }
            else if (request.getPathInfo().equals("/register")) {
                logger.info("register");

                httpMessageContext.cleanClientSubject(); // don't know if this is necessary

                return AuthenticationStatus.NOT_DONE;
            }
        }
        else {
            logger.info("existing session");

            if (request.getPathInfo().equals("/logout")) {
                logger.info("logout");
                session.invalidate();
                return AuthenticationStatus.NOT_DONE;
            }

            logger.info("other");

            return httpMessageContext.notifyContainerAboutLogin(
                (Principal) session.getAttribute("principal"),
                (Set<String>) session.getAttribute("groups")
            );
        }

        return httpMessageContext.responseUnauthorized();
    }
    
}
