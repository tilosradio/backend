package hu.tilos.radio.backend.auth;

import com.mongodb.*;
import hu.tilos.radio.backend.data.Author;
import hu.tilos.radio.backend.data.error.NotFoundException;
import org.bson.types.ObjectId;
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

    public UserInfo me(Session session) {
        if (session == null) {
            throw new NotFoundException("User is not logged in");
        }
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("username", session.getCurrentUser().getUsername()));
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

    public List<UserInfo> list() {
        DBCursor users = db.getCollection("user").find();
        List<UserInfo> userList = new ArrayList();
        while (users.hasNext()) {
            userList.add(mapper.mapUserInfo(users.next()));
        }
        return userList;
    }


}
