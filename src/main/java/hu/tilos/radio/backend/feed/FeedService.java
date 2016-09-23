package hu.tilos.radio.backend.feed;

import com.mongodb.DB;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.ShowType;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import net.anzix.jaxrs.atom.Feed;
import net.anzix.jaxrs.atom.Link;
import net.anzix.jaxrs.atom.MediaType;
import net.anzix.jaxrs.atom.itunes.Category;
import net.anzix.jaxrs.atom.itunes.Explicit;
import net.anzix.jaxrs.atom.itunes.Image;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

/**
 * Generate atom showFeed for the shows.
 */
@Service
public class FeedService {


    @Inject
    private EpisodeUtil episodeUtil;

    @Inject
    private DB db;

    @Inject
    private FeedRenderer feedRenderer;

    @Inject
    @Value("${server.url}")
    private String serverUrl;

    @Inject
    private DozerBeanMapper mapper;

    @Cacheable("feed-weekly")
    public Feed weeklyFeed(String format) {
        return weeklyFeed(null, format);
    }

    @Cacheable("feed-weekly-type")
    public Feed weeklyFeed(String type, String format) {
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);

        List<EpisodeData> episodes = filter(episodeUtil.getEpisodeData(null, weekAgo, now), type);

        Collections.sort(episodes, (episodeData, episodeData2) -> episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom()));

        Feed feed = feedRenderer.generateFeed(episodes,
                "urn:radio-tilos-hu:weekly" + (type == null ? "" : "." + type),
                "weekly" + (type == null ? "" : "-" + type),
                format,
                true);


        feed.setTitle("Tilos Rádió heti podcast");
        feed.setUpdated(new Date());

        Link feedLink = new Link();
        feedLink.setRel("self");
        feedLink.setType(new MediaType("application", "atom+xml"));
        feedLink.setHref(uri(serverUrl + "/showFeed/weekly"));

        return feed;
    }


    @Cacheable("feed-tilos")
    public Feed tilosFeed() {
        return tilosFeed(null, null);
    }

    @Cacheable("feed-tilos-type")
    public Feed tilosFeed(String type) {
        return tilosFeed(type, "mp3");
    }

    @Cacheable("feed-tilos-type-format")
    public Feed tilosFeed(String type, String format) {
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - 3L * 604800000L);

        List<EpisodeData> result = filter(episodeUtil.getEpisodeData(null, weekAgo, now), type);
        List<EpisodeData> episodes = result.stream()
                .sorted(new Comparator<EpisodeData>() {
                    @Override
                    public int compare(EpisodeData episodeData, EpisodeData episodeData2) {
                        return episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom());
                    }

                }).filter(episodeData -> {
                    return episodeData.getText() != null && !episodeData.getPlannedFrom().equals(episodeData.getRealFrom());
                })
                .collect(Collectors.toList());


        Feed feed = feedRenderer.generateFeed(
                episodes,
                "urn:radio-tilos-hu:podcast" + (type == null ? "" : "." + type)
                , "podcast" + (type == null ? "" : ("-" + type))
                , format
                , true);

        if (type == null) {
            feed.setTitle("Tilos Rádió podcast");
        } else if (type.equals("talk")) {
            feed.setTitle("Tilos Rádió szöveges podcast");
            feed.setSubtitle("Válogatás a Tilos Rádió legutóbbi szöveges adásaiból");
        } else if (type.equals("music")) {
            feed.setTitle("Tilos Rádió zenés podcast");
            feed.setSubtitle("Válogatás a Tilos Rádió legutóbbi zenés adásaiból");

        }
        feed.addAnyOther(new Category("Podcasts"));
        feed.setUpdated(new Date());
        feed.setImage(new Image("https://tilos.hu/images/podcast/tilos.jpg"));
        feed.addAnyOther(new net.anzix.jaxrs.atom.itunes.Category("Public Radio"));
        feed.addAnyOther(new Explicit());

        Link feedLink = new Link();
        feedLink.setRel("self");
        feedLink.setType(new MediaType("application", "atom+xml"));
        feedLink.setHref(uri(serverUrl + "/showFeed/tilos" + type == null ? "" : "/type"));

        return feed;
    }


    private List<EpisodeData> filter(List<EpisodeData> episodeData, String type) {
        if (type == null) {
            return episodeData;
        } else {
            List<EpisodeData> result = new ArrayList<>();
            for (EpisodeData data : episodeData) {
                if ((type.equals("talk") && data.getShow().getType() == ShowType.SPEECH) || (type.equals("music") && data.getShow().getType() == ShowType.MUSIC)) {
                    result.add(data);
                }
            }
            return result;
        }
    }

    private URI uri(String s) {
        try {
            return new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL can't be converted", e);
        }
    }


    public Feed itunesFeed(String alias) {
        return showFeed(alias, null, "show-feed");
    }


    @Cacheable("feed-show")
    public Feed showFeed(String alias, String selector, String format) {
        return showFeed(alias, null, selector, format);
    }

    public Feed showFeed(String alias, String year, String selector, String format) {
        //{year: (/.*)?
        //,
        if (year == null) {
            year = "";
        } else if (year.startsWith("/")) {
            year = year.substring(1);
        }

        ShowSimple show = mapper.map(db.getCollection("show").findOne(aliasOrId(alias)), ShowSimple.class);

        Date end;
        Date start;
        if ("".equals(year)) {
            end = getNow();
            //six monthes
            start = new Date();
            start.setTime(end.getTime() - (long) 60 * 24 * 30 * 6 * 60 * 1000);
        } else {
            int yearInt = Integer.parseInt(year);
            start = new Date(yearInt - 1900, 0, 1);
            end = new Date(yearInt - 1900 + 1, 0, 1);
        }

        List<EpisodeData> episodeData = episodeUtil.getEpisodeData("" + show.getId(), start, end);
        Collections.sort(episodeData, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData episodeData, EpisodeData episodeData2) {
                return episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom());
            }
        });


        Feed feed = feedRenderer.generateFeed(episodeData, "urn:radio-tilos-hu:show." + alias, "show" + "-" + alias + "-" + selector, format, false);

        //generate header

        feed.setTitle(show.getName() + " [Tilos Rádió podcast]");
        feed.setUpdated(new Date());

        String yearPostfix = ("".equals(year) ? "" : "/" + year);

        Link feedLink = new Link();
        feedLink.setRel("self");
        feedLink.setType(new MediaType("application", "atom+xml"));
        feedLink.setHref(uri(serverUrl + "/showFeed/show/" + show.getAlias() + yearPostfix));

        feed.getLinks().add(feedLink);
        feed.setId(uri("https://tilos.hu/show/" + show.getAlias() + yearPostfix));

        return feed;


    }

    protected Date getNow() {
        return new Date();
    }

    public EpisodeUtil getEpisodeUtil() {
        return episodeUtil;
    }

    public void setEpisodeUtil(EpisodeUtil episodeUtil) {
        this.episodeUtil = episodeUtil;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}
