package hu.tilos.radio.backend.auth;

import com.mongodb.*;
import hu.tilos.radio.backend.data.Author;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    @Inject
    BeanMapper mapper;

    @Inject
    DB db;

    @Inject
    AuthorRepository authorRepository;

    public UserInfo getUserByName(String username) {
        DBObject user = db.getCollection("user").find(new BasicDBObject("username", username)).one();
        UserInfo ui = new UserInfo();
        ui.setUsername((String) user.get("username"));
        ui.setId(((ObjectId) user.get("_id")).toHexString());
        ui.setRole(Role.values()[(int) user.get("role_id")]);
        return ui;
    }

    public UserInfo getUserByEmail(String email) {
        DBObject user = db.getCollection("user").find(new BasicDBObject("email", email)).one();
        if (user == null) {
            return null;
        }
        UserInfo ui = new UserInfo();
        ui.setUsername((String) user.get("username"));
        ui.setId(((ObjectId) user.get("_id")).toHexString());
        ui.setRole(Role.values()[(int) user.get("role_id")]);
        return ui;
    }

    public UserInfo getUser(String username) {
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("username", username));
        UserInfo user = mapper.mapUserInfo(userObject);
        AuthUtil.calculatePermissions(user);
        if (userObject.get("author") != null) {
            String id = ((ObjectId) ((DBRef) userObject.get("author")).getId()).toHexString();
            Author one = authorRepository.findById(id).get();
            user.setAuthor(one);
        }
        AuthUtil.calculatePermissions(user);
        return user;
    }

    public UserInfo getUserById(String userId) {
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("_id", new ObjectId(userId)));
        UserInfo user = mapper.mapUserInfo(userObject);
        AuthUtil.calculatePermissions(user);
        if (userObject.get("author") != null) {
            String id = ((ObjectId) ((DBRef) userObject.get("author")).getId()).toHexString();
            Author one = authorRepository.findById(id).get();
            user.setAuthor(one);
        }
        AuthUtil.calculatePermissions(user);
        return user;
    }

    public UserInfo me() {
        final UserInfo userInfo = getCurrentUser();
        if (userInfo == null) {
            throw new NotFoundException("User is not logged in");
        }
        return getUser(userInfo.getUsername());
    }

    public List<UserInfo> list() {
        DBCursor users = db.getCollection("user").find();
        List<UserInfo> userList = new ArrayList();
        while (users.hasNext()) {
            userList.add(mapper.mapUserInfo(users.next()));
        }
        return userList;
    }


    public UserInfo getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtToken) {
            JwtToken authToken = (JwtToken) auth;
            return authToken.getUserInfo();
        } else if (auth instanceof OAuth2AuthenticationToken) {
            final String email = ((DefaultOidcUser) auth.getPrincipal()).getAttribute("email");
            return getUserByEmail(email);
        }
        return null;
    }
}
