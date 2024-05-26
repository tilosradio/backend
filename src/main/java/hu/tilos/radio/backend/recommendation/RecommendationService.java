package hu.tilos.radio.backend.recommendation;

import com.mongodb.*;
import hu.tilos.radio.backend.auth.AuthorRepository;
import hu.tilos.radio.backend.auth.UserInfo;
import hu.tilos.radio.backend.data.Author;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.auth.Role;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private AuthorRepository authorRepository;

    public List<RecommendationSimple> list(String type) {
        BasicDBObject criteria = new BasicDBObject();

        // Filter by type
        if (!"all".equals(type) && type != null) {
            criteria.put("type", RecommendationType.valueOf(type).ordinal());
        }

        DBCursor selectedRecommendations = db.getCollection(COLLECTION).find(criteria);

        if (!"all".equals(type) && RecommendationType.EVENT == RecommendationType.valueOf(type)){
            selectedRecommendations.sort(new BasicDBObject("date", -1));
        } else {
            selectedRecommendations.sort(new BasicDBObject("created", -1));
        }

        List<RecommendationSimple> mappedRecommendations = new ArrayList<>();

        for (DBObject selectedRecommendation : selectedRecommendations) {
            selectedRecommendation.put("type", RecommendationType.values()[((Integer) selectedRecommendation.get("type"))]);
            selectedRecommendation.put("id", selectedRecommendation.get("_id").toString());

            mappedRecommendations.add(mapper.map(selectedRecommendation, RecommendationSimple.class));
        }

        for (RecommendationSimple recommendation : mappedRecommendations) {
            if (recommendation.getAuthor() != null) {
                Author author = authorRepository.findById(recommendation.getAuthor().getId()).get();
                recommendation.setAuthor(author);
            }
        }

        return mappedRecommendations;
    }

    public RecommendationData get(String id) {
        DBObject recommendationObject = db.getCollection(COLLECTION).findOne(new BasicDBObject("_id", new ObjectId(id)));

        if (recommendationObject == null) {
            throw new NotFoundException("Recommendation not found: " + id);
        }

        RecommendationData recommendation = mapper.map(recommendationObject, RecommendationData.class);

        if (recommendation.getAuthor() != null) {
            Author author = authorRepository.findById(recommendation.getAuthor().getId()).get();
            recommendation.setAuthor(author);
        }

        return recommendation;
    }

    public CreateResponse create(RecommendationToSave recommendationToSave, UserInfo userInfo) {
        Author author = null;

        if (userInfo.getAuthor() != null) {
            author = userInfo.getAuthor();
        }

        DBObject recommendation = mapper.map(recommendationToSave, BasicDBObject.class);

        recommendation.put("created", new Date());
        recommendation.put("author", author != null ? new DBRef("author", new ObjectId(author.getId())) : null);

        db.getCollection(COLLECTION).insert(recommendation);

        return new CreateResponse(((ObjectId) recommendation.get("_id")).toHexString());
    }

    public UpdateResponse update(String id, RecommendationToSave recommendationToSave, UserInfo userInfo) {

        // Is owner
        RecommendationData recommendation = get(id);
        if (recommendation.getAuthor() != null) {
            Author author = authorRepository.findById(recommendation.getAuthor().getId()).get();
            if (!author.getId().equals(recommendation.getAuthor().getId())) {
                throw new IllegalArgumentException("You are not the owner of this recommendation");
            }
        }

        DBObject recommendationObject = mapper.map(recommendationToSave, BasicDBObject.class);

        db.getCollection(COLLECTION).update(new BasicDBObject("_id", new ObjectId(id)), recommendationObject);

        return new UpdateResponse(true);
    }

    public OkResponse delete(String id, UserInfo userInfo) {
        RecommendationData recommendation = get(id);
        if (recommendation == null) {
            throw new IllegalArgumentException("Recommendation not found: " + id);
        }

        // Is owner
        if (userInfo.getRole() != Role.ADMIN) {
            Author author = authorRepository.findById(recommendation.getAuthor().getId()).get();
            if (!author.getId().equals(recommendation.getAuthor().getId())) {
                throw new IllegalArgumentException("You are not the owner of this recommendation");
            }
        }

        db.getCollection(COLLECTION).remove(new BasicDBObject("_id", new ObjectId(id)));

        return new OkResponse("Recommendation has been deleted");
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
