package hu.tilos.radio.backend.auth;

import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;


    private OAuth2AuthorizedClientService authorizedClientService;
//
//    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
//        this.authorizedClientService = authorizedClientService;
//    }

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
        return userService.me();
    }


    @GetMapping("/api/v1/oauth/client")
    OAuth2AuthorizedClient authorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }

    @GetMapping("/api/v1/oauth/info")
    Map<String, Object> moreInfo(Principal user) {
        return ((OAuth2AuthenticationToken) user).getPrincipal().getAttributes();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/user", method = RequestMethod.GET)
    public List<UserInfo> userList() {
        Session session = new Session();
        return userService.list();
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such element")
    @ExceptionHandler(NotFoundException.class)
    public void notFound() {
        // Nothing to do
    }

}
