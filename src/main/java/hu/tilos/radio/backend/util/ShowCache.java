package hu.tilos.radio.backend.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.ShowType;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShowCache {

    @Inject
    DB db;

    private static Map<String, ShowSimple> showCache = new ConcurrentHashMap<>();

    public ShowSimple getShowSimple(String id) {
        if (!showCache.containsKey(id)) {
            DBObject one = db.getCollection("show").findOne(new BasicDBObject("_id", new ObjectId(id)));
            ShowSimple show = new ShowSimple();
            show.setType(ShowType.values()[(Integer) one.get("type")]);
            show.setAlias((String) one.get("alias"));
            show.setName((String) one.get("name"));
            show.setId(id);
            showCache.put(show.getId(), show);
        }
        return showCache.get(id);
    }
}
