package hu.tilos.radio.backend.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import hu.tilos.radio.backend.auth.Role;
import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.auth.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JwtToken implements Authentication {

    private final SignedJWT sjwt;

    private JWTClaimsSet claims;

    private boolean authenticated;

    private UserInfo userInfo;

    public JwtToken(SignedJWT sjwt) {
        this.sjwt = sjwt;
        this.authenticated = false;
        try {
            claims = this.sjwt.getJWTClaimsSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clearClaims() {
        this.claims = new JWTClaimsSet.Builder().build();
    }

    public SignedJWT getSignedToken() {
        return this.sjwt;
    }

    @Override
    public String getName() {
        return this.claims.getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (userInfo != null) {
            if (userInfo.getRole() == Role.ADMIN) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_AUTHOR"));
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            if (userInfo.getRole() == Role.AUTHOR) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_AUTHOR"));
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            if (userInfo.getRole() == Role.USER) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        }
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        return Collections.unmodifiableList(grantedAuthorities);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return claims.toJSONObject();
    }

    @Override
    public Object getPrincipal() {
        return claims.getSubject();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public String getUsername() {
        try {
            return claims.getStringClaim("username");
        } catch (ParseException e) {
            throw new RuntimeException("Can't find the username in the jwt token", e);
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}

