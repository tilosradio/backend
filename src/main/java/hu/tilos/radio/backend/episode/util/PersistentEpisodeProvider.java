package hu.tilos.radio.backend.episode.util;


import com.mongodb.*;
import static hu.tilos.radio.backend.MongoUtil.aliasOrId;
import hu.tilos.radio.backend.episode.EpisodeData;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Returns with the persisted episode records.
 */
@Service
public class PersistentEpisodeProvider {


    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    public List<EpisodeData> listEpisode(String showIdOrAlias, Date from,
        Date to) {


        BasicDBObject query = new BasicDBObject();

        query.put("plannedFrom", new BasicDBObject("$lt", to));
        query.put("plannedTo", new BasicDBObject("$gt", from));

        if (showIdOrAlias != null) {

            BasicDBObject q = aliasOrId(showIdOrAlias);

            DBCursor shows = db.getCollection("show").find(q);
            if (!shows.hasNext()) {
                throw new IllegalArgumentException(
                    "No such show " + showIdOrAlias);

            }

            DBObject show = shows.next();
            System.out.println(show.get("_id"));
            query.put("show.ref.$id", show.get("_id").toString());
        }
        System.out.println(query.toString());
        DBCursor episodes = db.getCollection("episode").find(query);

        List<EpisodeData> result = new ArrayList<>();
        for (DBObject e : episodes) {
            EpisodeData d = mapper.map(e, EpisodeData.class);
            d.setPersistent(true);

            if (d.getPlannedTo() == d.getRealTo()) {
                //todo
                Date nd = new Date();
                nd.setTime(d.getPlannedTo().getTime() + 30 * 60 * 1000);
                d.setRealTo(nd);
            }
            result.add(d);
        }

        return result;

    }




    public void setDb(DB db) {
        this.db = db;
    }
}
