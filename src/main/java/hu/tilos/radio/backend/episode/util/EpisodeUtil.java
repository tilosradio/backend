package hu.tilos.radio.backend.episode.util;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBRef;
import hu.tilos.radio.backend.bookmark.BookmarkData;
import hu.tilos.radio.backend.data.types.TextData;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.stat.ListenerStat;
import hu.tilos.radio.backend.stat.StatService;
import hu.tilos.radio.backend.text.TextConverter;
import hu.tilos.radio.backend.util.ShowCache;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class EpisodeUtil {

    public static SimpleDateFormat YYYYMMDD = DateFormatUtil.create("yyyyMMdd");

    public static SimpleDateFormat HHMMSS = DateFormatUtil.create("HHmmss");

    public static SimpleDateFormat YYYY_MM_DD = DateFormatUtil.create("yyyy'/'MM'/'dd");

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(EpisodeUtil.class);

    @Inject
    protected PersistentEpisodeProvider persistentProvider;

    @Inject
    private ScheduledEpisodeProvider scheduledProvider;

    @Inject
    private ExtraEpisodeProvider extraProvider;

    @Inject
    StatService statService;

    @Inject
    private Merger merger = new Merger();

    @Inject
    ShowCache showCache;

    @Inject
    DB db;

    @Inject
    TextConverter converter;

    public EpisodeData enrichEpisode(EpisodeData r) {
        try {
            linkGenerator(r);
            //statCalculator(r);
            r.setShow(showCache.getShowSimple(r.getShow().getId()));
            if (r.getText() != null) {
                if (r.getText().getFormat() == null) {
                    r.getText().setFormat("legacy");
                }
                try {
                    r.getText().setFormatted(converter.format(r.getText().getFormat(), r.getText().getContent()));
                } catch (Exception ex) {
                    r.getText().setFormat("Can't render episode " + ex.getMessage());
                    LOG.error("Can't render episode " + r.getId() + " " + r.getPlannedFrom(), ex);
                }
                return r;
            }
        } catch (Exception ex) {
            LOG.error("Can't enrich episode: " + r.getId(), ex);
            throw ex;
        }
        return r;
    }

    private void statCalculator(EpisodeData episode) {
        if (episode.getStatListeners() == null) {
            ListenerStat listenerStat = statService.calculateListenerStats(episode);
            episode.setStatListeners(listenerStat);
        }
    }

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, Date from, Date to) {
        return getEpisodeData(showIdOrAlias, from, to, true);
    }

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, Date from, Date to, boolean persist) {
        List<EpisodeData> merged = merger.merge(
                persistentProvider.listEpisode(showIdOrAlias, from, to),
                scheduledProvider.listEpisode(showIdOrAlias, from, to),
                extraProvider.listEpisode(from, to)
        );


        merged = filterToShow(showIdOrAlias, merged);
        if (persist) {
            persistEpisodeFromThePast(merged);
        }
        merged = episodeTextFromBookmark(merged);
        for (EpisodeData episode : merged) {
            enrichEpisode(episode);
        }
        return merged;
    }


    private void addBookmarkTo(BookmarkData bookmark, List<EpisodeData> merged) {
        Optional<EpisodeData> episode = merged.stream().max((e1, e2) -> getIntersection(e1, bookmark).compareTo(getIntersection(e2, bookmark)));
        if (episode.isPresent()) {
            EpisodeData bestEpisode = episode.get();
            long intersection = getIntersection(bestEpisode, bookmark);
            long bookmarkDuration = (bookmark.getTo().getTime() - bookmark.getFrom().getTime()) / 1000;
            if (intersection * 2 > bookmarkDuration) {
                bestEpisode.getBookmarks().add(bookmark);
            }
        }

    }

    private Long getIntersection(EpisodeData episodeData, BookmarkData bookmark) {
        if (episodeData.getRealTo().after(bookmark.getFrom()) && bookmark.getTo().after(episodeData.getRealFrom())) {
            return (Math.min(episodeData.getRealTo().getTime(), bookmark.getTo().getTime()) - Math.max(episodeData.getRealFrom().getTime(), bookmark.getFrom().getTime())) / 1000;
        } else {
            return Long.valueOf(0);
        }
    }


    private boolean persistEpisodeFromThePast(List<EpisodeData> merged) {
        boolean persisted = false;
        for (EpisodeData episode : merged) {
            if (!episode.isPersistent() && episode.getPlannedFrom().getTime() < new Date().getTime()) {
                generateEpisode(episode);
            }
        }
        return persisted;
    }

    private void generateEpisode(EpisodeData episode) {
        BasicDBObject newMongoOBject = new BasicDBObject();
        newMongoOBject.put("created", new Date());
        newMongoOBject.put("plannedFrom", episode.getPlannedFrom());
        newMongoOBject.put("plannedTo", episode.getPlannedTo());
        newMongoOBject.put("realFrom", episode.getRealFrom());
        newMongoOBject.put("extra", false);
        newMongoOBject.put("realTo", episode.getRealTo());

        BasicDBObject show = new BasicDBObject();
        show.put("alias", episode.getShow().getAlias());
        show.put("name", episode.getShow().getName());
        show.put("ref",
            new DBRef("show", episode.getShow().getId()));

        episode.setPersistent(true);

        newMongoOBject.put("show", show);
        try {
            db.getCollection("episode").insert(newMongoOBject);
        } catch (Exception ex) {
            LOG.error("Can't persist generated episode: " + episode.getShow()
                .getAlias() + "/" + episode.getRealFrom(), ex);
        }
    }

    private List<EpisodeData> episodeTextFromBookmark(List<EpisodeData> original) {
        return original.stream().map(episodeData -> {
                    if ((episodeData.getText() == null || episodeData.getText().getTitle() == null) && episodeData.getBookmarks().size() > 0) {
                        BookmarkData bookmark = findBestBookmark(episodeData, episodeData.getBookmarks());
                        if (bookmark != null) {
                            bookmark.setSelected(true);
                            TextData text = new TextData();
                            text.setTitle(bookmark.getTitle());
                            episodeData.setText(text);
                            episodeData.setOriginal(false);
                            episodeData.setRealFrom(bookmark.getFrom());
                            episodeData.setRealTo(bookmark.getTo());
                        }
                    }
                    return episodeData;
                }
        ).collect(Collectors.toList());
    }

    private BookmarkData findBestBookmark(EpisodeData episodeData, Set<BookmarkData> bookmarks) {
        List<BookmarkData> ordered = new ArrayList();
        long episodeLength = episodeData.getRealTo().getTime() - episodeData.getRealFrom().getTime();
        for (BookmarkData bookmark : bookmarks) {
            long bookmarkLength = bookmark.getTo().getTime() - bookmark.getFrom().getTime();
            if (bookmarkLength * 2 > episodeLength) {
                ordered.add(bookmark);
            }
        }
        Collections.sort(ordered, new Comparator<BookmarkData>() {
            @Override
            public int compare(BookmarkData b1, BookmarkData b2) {
                return Long.valueOf(b2.getLengthInSec()).compareTo(Long.valueOf(b1.getLengthInSec()));
            }
        });
        if (ordered.size() > 0) {
            return ordered.get(0);
        } else {
            return null;
        }
    }

    private List<EpisodeData> filterToShow(String showIdOrAlias, List<EpisodeData> original) {
        if (showIdOrAlias != null) {
            return original.stream().filter(episodeData ->
                    episodeData.getShow() != null &&
                            (
                                    showIdOrAlias.equals(episodeData.getShow().getAlias())
                                            || showIdOrAlias.equals(episodeData.getShow().getId())
                            )
            ).collect(Collectors.toList());
        } else {
            return original;
        }
    }

    public static EpisodeData linkGenerator(EpisodeData episode) {
        if (episode.getRealTo().compareTo(new Date()) < 0) {
            episode.setM3uUrl(linkGenerator(episode.getRealFrom(), episode.getRealTo()));
        }
        return episode;
    }

    public static String linkGenerator(Date from, Date to) {
        return "https://archive.tilos.hu/mp3/tilos-" +
                YYYYMMDD.format(from) +
                "-" +
                HHMMSS.format(from) +
                "-" +
                HHMMSS.format(to) + ".m3u";

    }

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, LocalDateTime start, LocalDateTime end) {
        return getEpisodeData(showIdOrAlias, Date.from(start.atZone(ZoneId.systemDefault()).toInstant()), Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));

    }
}
