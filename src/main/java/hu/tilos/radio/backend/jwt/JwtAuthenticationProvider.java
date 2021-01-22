package hu.tilos.radio.backend.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import hu.tilos.radio.backend.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Value("${jwt.secret}")
    private String SecretKey;

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (authentication instanceof JwtToken) {
            JwtToken jwtToken = (JwtToken) authentication;
            try {
                JWSVerifier verifier = new MACVerifier(SecretKey);
                boolean isVerified = jwtToken.getSignedToken().verify(verifier);
                if (isVerified) {
                    jwtToken.setAuthenticated(true);
                    jwtToken.setUserInfo(userService.getUserByName(jwtToken.getUsername()));
                } else {
                    throw new JwtAuthenticationException("authentication failed");
                }

                return jwtToken;
            } catch (JOSEException e) {
                throw new JwtAuthenticationException("authentication failed: " + e.getMessage());
            }
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtToken.class);
    }

}