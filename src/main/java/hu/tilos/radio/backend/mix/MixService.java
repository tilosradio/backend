package hu.tilos.radio.backend.mix;

import com.mongodb.*;
import hu.tilos.radio.backend.contribution.ShowReference;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class MixService {

    private static final String LOCATION = "http://archive.tilos.hu/sounds/";
    private static Logger LOG = LoggerFactory.getLogger(MixService.class);
    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DB db;

    public List<Mix> list(String category) {

        DBCursor mixes = db.getCollection("mix").find(new BasicDBObject("category", MixCategory.valueOf(category.toUpperCase()).ordinal()));

        List<Mix> result = new ArrayList<>();
        for (DBObject mix : mixes) {
            result.add(fromMongoMix(mix));
        }

        return result;

    }

    private ShowReference fromMongoShowReference(DBObject obj) {
        ShowReference showReference = new ShowReference();
        if (obj == null) {
            return null;
        }
        DBRef ref = (DBRef) obj.get("ref");
        if (ref == null) {
            return null;
        }
        showReference.setId(ref.getId().toString());
        showReference.setName((String) obj.get("name"));
        showReference.setAlias((String) obj.get("alias"));
        return showReference;
    }

    private Mix fromMongoMix(DBObject obj) {
        if (obj == null) {
            return null;
        }
        Mix mix = new Mix();
        mix.setId(obj.get("_id").toString());
        mix.setCategory(MixCategory.values()[(int) obj.get("category")]);
        mix.setType(MixType.values()[(int) obj.get("type")]);
        mix.setTitle((String) obj.get("title"));
        mix.setFile((String) obj.get("file"));
        mix.setAuthor((String) obj.get("author"));
        mix.setContent((String) obj.get("content"));
        mix.setWithContent(mix.getContent() != null && mix.getContent().length() > 0);
        mix.setLink(fixLink((String) obj.get("file")));
        mix.setShow(fromMongoShowReference((DBObject) obj.get("show")));
        return mix;
    }

    private String fixLink(String file) {
        if (!file.startsWith(LOCATION)) {
            return LOCATION + file;
        }
        return file;
    }


    public void setDb(DB db) {
        this.db = db;
    }

    public Mix get(String id) {
        return fromMongoMix(db.getCollection("mix").findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
