package hu.tilos.radio.backend.auth;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import hu.tilos.radio.backend.data.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


@Service
public class OauthService {

    private static final Logger LOG = LoggerFactory.getLogger(OauthService.class);

    //@Inject
    Session session;

    @Inject
    DB db;

    @Inject
    private JWTEncoder jwtEncoder;

    @Inject
    @Value("${facebook.key}")
    private String clientId;

    @Inject
    @Value("${facebook.secret}")
    private String clientKey;

    @Inject
    @Value("${server.url}")
    private String serverUrl;

    public Map<String, String> facebook(FacebookRequest request) {
        try {

            String accessToken = getAccessToken(request.code);

            DBObject userResponse = saveOrGetUser(accessToken);

            return createToken(userResponse);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Map<String, String> createToken(DBObject userResponse) {
        Map result = new HashMap<>();
        Token jwtToken = new Token();
        jwtToken.setUsername((String) userResponse.get("username"));
        jwtToken.setRole(Role.USER);
        result.put("token", jwtEncoder.encode(jwtToken));
        return result;
    }

    private DBObject saveOrGetUser(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);

        User me = facebookClient.fetchObject("me", User.class);
        LOG.debug("Facebook profile is downloaded: " + me + " with access_token " + accessToken);
        DBObject userResponse = db.getCollection("user").findOne(new BasicDBObject("facebook", me.getId()));

        if (userResponse == null) {
            BasicDBObject newUser = new BasicDBObject()
                    .append("facebook", me.getId())
                    .append("username", me.getEmail() != null ? me.getEmail().split("@")[0] : me.getName())
                    .append("email", me.getEmail())
                    .append("link", me.getLink())
                    .append("role_id", Role.USER.ordinal());
            db.getCollection("user").insert(newUser);
            userResponse = db.getCollection("user").findOne(new BasicDBObject("facebook", me.getId()));
        }
        return userResponse;
    }

    private String getAccessToken(String code) throws IOException {
        //change to an access token
        String url = String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", clientId, clientKey, code, serverUrl + "/");
        URLConnection uc = new URL(url).openConnection();
        String response = new Scanner(uc.getInputStream()).useDelimiter("\\Z").next();

        String[] parts = response.split("&");
        return parts[0].split("=")[1];
    }

    public static class FacebookRequest {

        private String code;

        private String clientId;

        private String redirectUri;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }


}
