package hu.tilos.radio.backend.episode;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.ShowType;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.stat.StatService;
import hu.tilos.radio.backend.tag.TagData;
import hu.tilos.radio.backend.tag.TagUtil;
import hu.tilos.radio.backend.util.ShowCache;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Service
public class EpisodeService {

    private static final Logger LOG = LoggerFactory.getLogger(EpisodeService.class);

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    TagUtil tagUtil;

    @Inject
    StatService statService;

    @Inject
    DB db;

    @Inject
    ShowCache showCache;

    private static final SimpleDateFormat YYYYMDD = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss");


    public EpisodeData get(String id) {
        DBObject episode = db.getCollection("episode").findOne(aliasOrId(id));
        EpisodeData r = modelMapper.map(episode, EpisodeData.class);
        episodeUtil.enrichEpisode(r);
        return r;
    }

    @Cacheable("episodes")
    public List<EpisodeData> listEpisodes(String showAlias, long from, long to) {
        Date fromDate = new Date();
        fromDate.setTime(from);
        Date toDate = new Date();
        toDate.setTime(to);
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(showAlias, fromDate, toDate);
        Collections.sort(episodeData, (e1, e2) -> e1.getPlannedFrom().compareTo(e2.getPlannedFrom()) * -1);
        return episodeData;

    }

    public List<EpisodeData> listEpisodes(long from, long to) {
        Date fromDate = new Date();
        fromDate.setTime(from);
        Date toDate = new Date();
        toDate.setTime(to);

        if (to - from > 1000 * 60 * 60 * 168) {
            throw new IllegalArgumentException("Period is too big");
        }
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(null, fromDate, toDate);
        Collections.sort(episodeData, (e1, e2) -> e1.getPlannedFrom().compareTo(e2.getPlannedFrom()));
        return episodeData;

    }

    public EpisodeData now() {
        OffsetDateTime start = OffsetDateTime.now().minusHours(3);
        OffsetDateTime end = OffsetDateTime.now().plusHours(4);


        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(null, Date.from(start.toInstant()), Date.from(end.toInstant()));

        Date now = new Date();
        EpisodeData result = null;
        for (int i = 0; i < episodeData.size(); i++) {
            EpisodeData episode = episodeData.get(i);
            if (episode.getPlannedFrom().compareTo(now) < 0) {
                result = episode;
            }
        }
        ;

        return result;

    }

    public OkResponse cleanupEpisodes(boolean force) {
        DBCursor episodes = db.getCollection("episode").find().sort(new BasicDBObject("plannedFrom", 1));
        EpisodeData prev = new EpisodeData();
        int i = 0;
        int c = 0;
        for (DBObject episode : episodes) {
            EpisodeData current = modelMapper.map(episode, EpisodeData.class);
            c++;
            if (prev != null) {
                if (prev.getPlannedFrom() != null && prev.getPlannedFrom().equals(current.getPlannedFrom())) {
                    i++;
                    printProblem("Duplicated episode", prev, current);
                    EpisodeData toDelete = findDuplicated(prev, current, force);
                    if (toDelete != null) {
                        LOG.info(String.format(String.format("To delete: %s ", toDelete.getId())));
                        db.getCollection("episode").remove(new BasicDBObject("_id", new ObjectId(toDelete.getId())));
                    }
                }
            }
            prev = current;
        }
        return new OkResponse(String.format("Found %d problem from %d record", i, c));
    }

    public OkResponse removeOverlap() {
        Date start = new Date(110, 04, 01);
        Date end = new Date(116, 01, 01);
        List<EpisodeData> episodes = episodeUtil.getEpisodeData(null, start, end, false);
        EpisodeData prev = null;
        int i = 0;
        for (EpisodeData current : episodes) {
            if (prev != null) {
                if (current.getPlannedFrom().getTime() == prev.getPlannedFrom().getTime() && current.isExtra() == prev.isExtra() && current.isExtra() == false) {
                    i++;
                    printProblem("Duplicated episode", prev, current);
                    long diff = prev.getPlannedTo().getTime() - current.getPlannedTo().getTime();
                    if (!current.isPersistent() && prev.isPersistent() && diff == 30 * 60 * 1000) {
                        db.getCollection("episode").update(new BasicDBObject("_id", new ObjectId(prev.getId())), new BasicDBObject("$set", new BasicDBObject("plannedTo", current.getPlannedTo())), false, false);
                    }

                }
                if (current.getPlannedFrom().getTime() != prev.getPlannedTo().getTime()) {
                    i++;
                    printProblem("Overlapping episode", prev, current);
                }
            }
            prev = current;
        }
        return new OkResponse(String.format("Found %d problem", i));
    }

    private void printProblem(String problem, EpisodeData prev, EpisodeData current) {
        LOG.error(String.format("%s: %s(%s)/%s(%s) %tF %tR-%tR %tR-%tR",
                problem,
                prev.getId(),
                prev.getShow().getName(),
                current.getId(),
                current.getShow().getName(),
                prev.getPlannedFrom(),
                prev.getPlannedFrom(),
                prev.getPlannedTo(),
                current.getPlannedFrom(),
                current.getPlannedTo()));
    }

    private EpisodeData findDuplicated(EpisodeData prev, EpisodeData current, boolean force) {
        if (deletable(prev)) {
            return prev;
        }
        if (deletable(current)) {
            return current;
        }
        if (force) {
            if (current.getText() != null && prev.getText() == null) {
                return prev;
            } else if (current.getText() == null && prev.getText() != null) {
                return current;
            } else if (current.getText() != null && prev.getText() != null) {
                if (prev.getText().getContent().length() > current.getText().getContent().length()) {
                    return current;
                } else {
                    return prev;
                }
            }
            return prev;
        }
        return null;
    }

    private boolean deletable(EpisodeData episode) {
        return episode.getBookmarks().size() == 0 && episode.getText() == null && !episode.isExtra() && episode.getPlannedFrom().equals(episode.getRealFrom());
    }

    @Cacheable("episodes-next")
    public List<EpisodeData> next() {
        Date start = new Date();
        Date end = new Date();
        end.setTime(start.getTime() + (long) 168 * 60 * 60 * 1000);

        BasicDBObject query = new BasicDBObject();
        query.put("plannedFrom", new BasicDBObject("$lt", end));
        query.put("plannedTo", new BasicDBObject("$gt", start));
        query.put("text.content", new BasicDBObject("$exists", true));

        DBCursor episodes = db.getCollection("episode").find(query).sort(new BasicDBObject("plannedFrom", 1)).limit(5);
        List<EpisodeData> result = new ArrayList<>();
        for (DBObject episode : episodes) {
            EpisodeData episodeData = modelMapper.map(episode, EpisodeData.class);
            episodeUtil.enrichEpisode(episodeData);
            result.add(episodeData);
        }
        return result;
    }


    public String previousWeekAsCsv() {

        LocalDateTime start = LocalDateTime.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY.MONDAY)).minusDays(7);
        LocalDateTime end = start.plusDays(7);

        List<EpisodeData> episodes = episodeUtil.getEpisodeData(null, start, end);

        Collections.sort(episodes, (episodeData, episodeData2) -> episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom()));

        StringBuilder b = new StringBuilder();
        episodes.forEach(episodeData -> {
            Date to = episodeData.getRealTo();
            if (episodeData.getRealTo().getTime() - episodeData.getPlannedTo().getTime() == 1000 * 60 * 30) {
                to = episodeData.getPlannedTo();
            }
            b.append(YYYYMDD.format(episodeData.getPlannedFrom()));
            b.append(";");
            b.append(HHMMSS.format(episodeData.getRealFrom()));
            b.append(";");
            b.append(HHMMSS.format(to));
            b.append(";");
            b.append(episodeData.getShow().getName());
            b.append(";");
            if (episodeData.getText() != null) {
                b.append(episodeData.getText().getTitle().replace(';', '-'));
            }
            b.append(";");
            b.append(episodeData.getShow().getType() == ShowType.SPEECH ? "Szöveges" : "Zenés");
            b.append("\n");
        });
        return b.toString();


    }

    @Cacheable("episodes-lastweek")
    public List<EpisodeData> lastWeek() {
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);

        List<EpisodeData> episodes = episodeUtil.getEpisodeData(null, weekAgo, now);

        Collections.sort(episodes, (episodeData, episodeData2) -> episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom()));

        List<EpisodeData> result = episodes.stream().filter(episode -> episode.getText() != null && episode.getText().getTitle() != null && episode.getText().getTitle().length() > 1)
                .collect(Collectors.toList());
        return result;
    }

    @Cacheable("episodes-last")
    public List<EpisodeData> last() {
        Date start = new Date();
        start.setTime(start.getTime() - (long) 30 * 24 * 60 * 60 * 1000);

        BasicDBObject query = new BasicDBObject();
        query.put("created", new BasicDBObject("$gt", start));
        query.put("text.content", new BasicDBObject("$exists", true));
        query.put("plannedFrom", new BasicDBObject("$lt", new Date()));

        DBCursor episodes = db.getCollection("episode").find(query).sort(new BasicDBObject("created", -1)).limit(10);
        List<EpisodeData> result = new ArrayList<>();
        for (DBObject episode : episodes) {
            EpisodeData episodeData = modelMapper.map(episode, EpisodeData.class);
            episodeUtil.enrichEpisode(episodeData);
            result.add(episodeData);
        }
        return result;
    }


    public EpisodeData getByDate(String showAlias, int year, int month, int day) {
        try {
            BasicDBObject show = (BasicDBObject) db.getCollection("show").findOne(aliasOrId(showAlias));
            String showId = show.get("_id").toString();
            String fromDate = String.format("%04d-%02d-%02d 00:00:00", year, month, day);
            String toDate = String.format("%04d-%02d-%02d 23:59:59", year, month, day);
            List<EpisodeData> episodeData = episodeUtil.getEpisodeData(showId, DateFormatUtil.YYYY_MM_DD_HHMM.parse(fromDate), DateFormatUtil.YYYY_MM_DD_HHMM.parse(toDate));
            ;
            if (episodeData.size() == 0) {
                throw new IllegalArgumentException("Can't find the appropriate episode");
            } else {
                return episodeData.get(0);
            }
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }


    public CreateResponse create(EpisodeToSave entity) {


        if (entity.getText() != null) {
            entity.getText().setFormat("markdown");
            entity.getText().setType("episode");
        }
        if (entity.getRealFrom() == null) {
            entity.setRealFrom(entity.getPlannedFrom());
        }
        if (entity.getRealTo() == null) {
            entity.setRealTo(entity.getPlannedTo());
        }


        updateTags(entity);
        BasicDBObject newMongoOBject = modelMapper.map(entity, BasicDBObject.class);
        newMongoOBject.put("created", new Date());
        newMongoOBject.remove("id");
        newMongoOBject.remove("persistent");

        db.getCollection("episode").insert(newMongoOBject);

        return new CreateResponse(((ObjectId) newMongoOBject.get("_id")).toHexString());
    }


    public UpdateResponse update(String alias, EpisodeToSave objectToSave) {

        if (objectToSave.getText() != null) {
            objectToSave.getText().setFormat("markdown");
            objectToSave.getText().setType("episode");
        }

        updateTags(objectToSave);
        DBObject original = db.getCollection("episode").findOne(aliasOrId(alias));


        if (objectToSave.isInThePast()) {
            original.put("statListeners", modelMapper.map(statService.calculateListenerStats(objectToSave), BasicDBObject.class));
        }

        modelMapper.map(objectToSave, original);
        db.getCollection("episode").update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }


    public void updateTags(EpisodeToSave episode) {
        if (episode.getText() != null && episode.getText().getContent() != null) {
            Set<TagData> newTags = tagUtil.getTags(episode.getText().getContent());
            episode.setTags(new ArrayList<TagData>(newTags));
        }
    }


    public void setDb(DB db) {
        this.db = db;
    }
}
