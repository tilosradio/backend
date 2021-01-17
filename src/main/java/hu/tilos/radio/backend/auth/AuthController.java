package hu.tilos.radio.backend.auth;

import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    @Inject
    OauthService oauthService;

    @RequestMapping(value = "/api/v1/auth/password_reset", method = RequestMethod.POST)
    public OkResponse passwordReset(@RequestBody PasswordReset passwordReset) {
        return authService.passwordReset(passwordReset);
    }

    @RequestMapping(value = "/api/v1/auth/login", method = RequestMethod.POST)
    public Map<String, String> login(@RequestBody LoginData login) {
        return authService.login(login);
    }

    @RequestMapping(value = "/api/v1/auth/register", method = RequestMethod.POST)
    public Map<String, String> register(@RequestBody RegisterData register) {
        return authService.register(register);
    }

    @RequestMapping(value = "/api/v1/user/me", method = RequestMethod.GET)
    public UserInfo profile() {
        return userService.me(getCurrentSession());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/user", method = RequestMethod.GET)
    public List<UserInfo> userList() {
        Session session = new Session();
        return userService.list();
    }

    @RequestMapping(value = "/api/int/oauth/facebook", method = RequestMethod.POST)
    public Map<String, String> facebook(@RequestBody OauthService.FacebookRequest request) {
        return oauthService.facebook(request);
    }

    public Session getCurrentSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtToken) {
            JwtToken authToken = (JwtToken) auth;
            return authToken.getSession();
        }
        return null;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such element")
    @ExceptionHandler(NotFoundException.class)
    public void notFound() {
        // Nothing to do
    }

}
