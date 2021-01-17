package hu.tilos.radio.backend.auth;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BeanMapper {

    public UserDetailed mapUserDetailed(DBObject userRaw) {
        UserDetailed detailed = new UserDetailed();
        detailed.setId(((ObjectId) userRaw.get("_id")).toHexString());
        detailed.setEmail((String) userRaw.get("email"));
        detailed.setUsername((String) userRaw.get("username"));
        detailed.setRole(Role.values()[(int) userRaw.get("role_id")]);
        if (userRaw.get("passwordChangeTokenCreated")!=null) {
            detailed.setPasswordChangeTokenCreated((Date) userRaw.get("passwordChangeTokenCreated"));
        }
        return detailed;
    }

    public UserInfo mapUserInfo(DBObject userRaw) {
        UserInfo detailed = new UserInfo();
        detailed.setId(((ObjectId) userRaw.get("_id")).toHexString());
        detailed.setEmail((String) userRaw.get("email"));
        detailed.setUsername((String) userRaw.get("username"));
        detailed.setRole(Role.values()[(int) userRaw.get("role_id")]);
        return detailed;
    }
}
