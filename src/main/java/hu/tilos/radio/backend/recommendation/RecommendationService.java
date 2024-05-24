package hu.tilos.radio.backend.recommendation;

import com.mongodb.*;
import hu.tilos.radio.backend.auth.UserInfo;
import hu.tilos.radio.backend.data.Author;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class RecommendationService {
    private static Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    private static final String COLLECTION = "recommendation";

    public List<RecommendationSimple> list(String type) {
        BasicDBObject criteria = new BasicDBObject();

        // Filter by type
        if (!"all".equals(type) && type != null) {
            criteria.put("type", RecommendationType.valueOf(type).ordinal());
        }

        DBCursor selectedRecommendations = db.getCollection(COLLECTION).find(criteria).sort(new BasicDBObject("created", -1));

        List<RecommendationSimple> mappedRecommendations = new ArrayList<>();

        for (DBObject selectedRecommendation : selectedRecommendations) {
            selectedRecommendation.put("type", RecommendationType.values()[((Integer) selectedRecommendation.get("type"))]);
            selectedRecommendation.put("id", selectedRecommendation.get("_id").toString());

            mappedRecommendations.add(mapper.map(selectedRecommendation, RecommendationSimple.class));
        }

        /*Collections.sort(mappedRecommendations, new Comparator<RecommendationSimple>() {
            @Override
            public int compare(RecommendationSimple s1, RecommendationSimple s2) {
                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });*/
        return mappedRecommendations;

    }

    public RecommendationData get(String id) {
        DBObject recommendationObject = db.getCollection(COLLECTION).findOne(new BasicDBObject("_id", new ObjectId(id)));

        if (recommendationObject == null) {
            throw new NotFoundException("Recommendation not found: " + id);
        }

        RecommendationData recommendation = mapper.map(recommendationObject, RecommendationData.class);

        return recommendation;
    }

    public CreateResponse create(RecommendationToSave recommendationToSave, UserInfo userInfo) {
        Author author;

        /*author = recommendationToSave.getAuthor();*/

        /*if (recommendationToSave.getAuthor() != null) {
            author = recommendationToSave.getAuthor();
        } else if (userInfo.getAuthor() != null) {
            author = userInfo.getAuthor();
        } else {
            throw new RuntimeException("Only authors can create recommendation, or you can specify the author in the request.");
        }*/

        DBObject recommendation = mapper.map(recommendationToSave, BasicDBObject.class);

        /*BasicDBObject authorObj = new BasicDBObject();
        authorObj.put("alias", author.getAlias());
        authorObj.put("name", author.getName());
        authorObj.put("ref", new DBRef("author", author.getId()));
        recommendation.put("author", authorObj);*/

        recommendation.put("created", new Date());

        db.getCollection(COLLECTION).insert(recommendation);

        return new CreateResponse(((ObjectId) recommendation.get("_id")).toHexString());
    }

    public UpdateResponse update(String id, RecommendationToSave recommendationToSave) {
        /*Author author = recommendationToSave.getAuthor();*/

        DBObject recommendationObject = mapper.map(recommendationToSave, BasicDBObject.class);

        /*BasicDBObject authorObj = new BasicDBObject();
        authorObj.put("alias", author.getAlias());
        authorObj.put("name", author.getName());
        authorObj.put("ref", new DBRef("author", author.getId()));*/
        /*recommendationObject.put("author", authorObj);*/

        /*DBObject recommendationObject = mapper.map(recommendationToSave, BasicDBObject.class);*/
        db.getCollection(COLLECTION).update(new BasicDBObject("_id", new ObjectId(id)), recommendationObject);

        return new UpdateResponse(true);
    }

    public OkResponse delete(String id) {
        RecommendationData recommendation = get(id);
        if (recommendation == null) {
            throw new IllegalArgumentException("Recommendation not found: " + id);
        }

        db.getCollection(COLLECTION).remove(new BasicDBObject("_id", new ObjectId(id)));

        return new OkResponse("Recommendation has been deleted");
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
