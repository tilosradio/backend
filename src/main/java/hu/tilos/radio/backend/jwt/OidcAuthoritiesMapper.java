package hu.tilos.radio.backend.jwt;

import hu.tilos.radio.backend.auth.Role;
import hu.tilos.radio.backend.auth.UserInfo;
import hu.tilos.radio.backend.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OidcAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Autowired
    UserService userService;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> collection) {
        List<GrantedAuthority> authorities = new ArrayList<>(collection);
        final UserInfo currentUser = userService.getCurrentUser();
        for (GrantedAuthority a : collection) {
            if (a instanceof OidcUserAuthority) {
                OidcUserAuthority user = (OidcUserAuthority) a;
                 String email = user.getAttributes().get("email").toString();
                final UserInfo userInfi = userService.getUserByEmail(email);
                if (userInfi.getRole() == Role.ADMIN) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }
        }
        return authorities;
    }
}
