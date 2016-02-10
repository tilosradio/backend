package hu.tilos.radio.backend.stat;

import com.mongodb.*;
import hu.tilos.radio.backend.episode.EpisodeBase;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class StatService {

    private static final Logger LOG = LoggerFactory.getLogger(StatService.class);

    @Inject
    DB db;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    DozerBeanMapper mapper;


    public List<ListenerStatWithEpisode> getListenerStatOfEpisodes(Long fromTimestamp, Long toTimestamp) {

        List<ListenerStatWithEpisode> result = new ArrayList<>();

        Date toDate = new Date();
        Date fromDate = new Date();
        fromDate.setTime(toDate.getTime() - (long) 7L * 60 * 60 * 24 * 1000);
        if (fromTimestamp != null) {
            fromDate.setTime(fromTimestamp);
        }
        if (toTimestamp != null) {
            toDate.setTime(toTimestamp);
        }

        fixUnderscore(fromDate, toDate);

        List<EpisodeData> episodeList = episodeUtil.getEpisodeData(null, fromDate, toDate);
        for (EpisodeData episode : episodeList) {
            try {
                result.add(new ListenerStatWithEpisode(episode, calculateListenerStats(episode)));
            } catch (Exception ex) {
                LOG.error("Can't calculate listening stat for " + episode.getPlannedFrom(), ex);
            }
        }
        return result;
    }


    private ListenerStat calculateListenerStats(Date from, Date to) {
        List<DBObject> pipeline = new ArrayList<>();

        DBObject match = new BasicDBObject("$match", QueryBuilder.start().put("time").greaterThan(from).lessThan(to).get());
        pipeline.add(match);
        BasicDBList fields = new BasicDBList();
        fields.add("$tilos");
        fields.add("$tilos_128_mp3");
        fields.add("$tilos_32_mp3");
        BasicDBObject group = new BasicDBObject().append("_id", null);
        group.append("min", new BasicDBObject("$min", new BasicDBObject("$add", fields)));
        group.append("max", new BasicDBObject("$max", new BasicDBObject("$add", fields)));
        group.append("avg", new BasicDBObject("$avg", new BasicDBObject("$add", fields)));
        pipeline.add(new BasicDBObject("$group", group));
        AggregationOutput stat_icecast = db.getCollection("stat_icecast").aggregate(pipeline);

        ListenerStat stat = new ListenerStat();
        for (DBObject o : stat_icecast.results()) {
            if (o.get("max") != null) {
                stat.setMax((Integer) o.get("max"));
            }
            if (o.get("min") != null) {
                stat.setMin((Integer) o.get("min"));
            }
            if (o.get("avg")!=null) {
                stat.setMean((int) Math.round((Double) o.get("avg")));
            }
        }
        return stat;
    }

    public ListenerStat calculateListenerStats(EpisodeBase episode) {
        long from = episode.getPlannedFrom().getTime();
        if (episode.getPlannedFrom().equals(episode.getRealFrom())) {
            from += 60 * 7 * 1000; //7 min
        }

        return calculateListenerStats(new Date(from), episode.getPlannedTo());
    }

    private void fixUnderscore(Date fromDate, Date toDate) {
        DBCollection icecast = db.getCollection("stat_icecast");
        DBCursor stat_icecast = icecast.find(QueryBuilder.start().put("time").greaterThan(fromDate).lessThan(toDate).get());
        while (stat_icecast.hasNext()) {
            DBObject next = stat_icecast.next();
            boolean update = false;
            update = fixField("tilos_128.mp3", next) || update;
            update = fixField("tilos_32.mp3", next) || update;
            if (update) {
                icecast.update(new BasicDBObject("_id", next.get("_id")), next);
            }
        }
    }

    private boolean fixField(String field, DBObject record) {
        if (record.containsField(field)) {
            record.put(field.replace('.', '_'), record.get(field));
            record.removeField(field);
            return true;
        }
        return false;
    }

    private int convertToInt(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new IllegalArgumentException("Can't convert " + value.getClass() + " to int");
        }
    }
}
