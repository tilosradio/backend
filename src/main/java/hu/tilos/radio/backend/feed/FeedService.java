package hu.tilos.radio.backend.feed;

import com.mongodb.DB;
import com.rometools.modules.itunes.EntryInformation;
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.modules.itunes.FeedInformationImpl;
import com.rometools.modules.itunes.types.Duration;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.ShowType;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.show.ShowDetailed;
import net.anzix.jaxrs.atom.*;
import net.anzix.jaxrs.atom.itunes.Category;
import net.anzix.jaxrs.atom.itunes.Explicit;
import net.anzix.jaxrs.atom.itunes.Image;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public String weeklyFeed(String format) {
        return weeklyFeed(null, format);
    }

    @Cacheable("feed-weekly-type")
    public String weeklyFeed(String type, String format) {

        return "notyet";
//
//        Date now = new Date();
//        Date weekAgo = new Date();
//        weekAgo.setTime(now.getTime() - (long) 604800000L);
//
//        List<EpisodeData> episodes = filter(episodeUtil.getEpisodeData(null, weekAgo, now), type);
//
//        Collections.sort(episodes, (episodeData, episodeData2) -> episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom()));
//
//        Feed feed = feedRenderer.generateFeed(episodes,
//                "urn:radio-tilos-hu:weekly" + (type == null ? "" : "." + type),
//                "weekly" + (type == null ? "" : "-" + type),
//                format,
//                true);
//
//
//        feed.setTitle("Tilos Rádió heti podcast");
//        feed.setUpdated(new Date());
//
//        feed.setImage(new Image(getThumbnail()));
//        feed.addAnyOther(new net.anzix.jaxrs.atom.itunes.Category("Society & Culture"));
//        feed.addAnyOther(new Explicit());
//
//        Link feedLink = new Link();
//        feedLink.setRel("self");
//        feedLink.setType(new MediaType("application", "atom+xml"));
//        String feedUrl = serverUrl + "/feed/weekly";
//        if (type != null) {
//            feedUrl = feedUrl + "/" + type;
//        }
//        feedLink.setHref(uri(feedUrl));
//        feed.getLinks().add(feedLink);
//
//        return feed;
    }


    @Cacheable("feed-tilos")
    public String tilosFeed() {
        return tilosFeed(null, null);
    }

    @Cacheable("feed-tilos-type")
    public String tilosFeed(String type) {
        return tilosFeed(type, "mp3");
    }

    @Cacheable("feed-tilos-type-format")
    public String tilosFeed(String type, String format) {
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


        SyndFeed feed = feedRenderer.generateFeed(
                episodes,
                "urn:radio-tilos-hu:podcast" + (type == null ? "" : "." + type)
                , "podcast" + (type == null ? "" : ("-" + type))
                , format
                , true);


        ArrayList modules = new ArrayList();
        FeedInformation iTunes = new FeedInformationImpl();
        String categoryName = "Society & Culture";

        if (type == null) {
            feed.setTitle("Tilos Rádió podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi adásaiból";
            feed.setDescription(description);
            iTunes.setSummary(description);
        } else if (type.equals("talk")) {
            feed.setTitle("Tilos Rádió szöveges podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi szöveges adásaiból";
            feed.setDescription(description);
            iTunes.setSummary(description);
        } else if (type.equals("music")) {
            feed.setTitle("Tilos Rádió zenés podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi zenés adásaiból";
            feed.setDescription(description);
            iTunes.setSummary(description);
            categoryName = "Music";
        }

        ArrayList<com.rometools.modules.itunes.types.Category> categories = new ArrayList();
        com.rometools.modules.itunes.types.Category category = new com.rometools.modules.itunes.types.Category();
        category.setName(categoryName);
        categories.add(category);
        iTunes.setCategories(categories);

        feed.setPublishedDate(new Date());
        iTunes.setImageUri(getThumbnail());
        iTunes.setExplicit(false);


//        Link feedLink = new Link();
//        feedLink.setRel("self");
//        feedLink.setType(new MediaType("application", "atom+xml"));
        String feedUrl = serverUrl + "/feed/podcast";
        if (type != null) {
            feedUrl = feedUrl + "/" + type;
        }

        feed.setLink(feedUrl);
        feed.setUri(feedUrl);

//        feedLink.setHref(uri(feedUrl));
//        feed.getLinks().add(feedLink);


        return outputFeed(feed);
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


    public String itunesFeed(String alias) {
        return showFeed(alias, null, "show-feed");
    }


    @Cacheable("feed-show")
    public String showFeed(String alias, String selector, String format) {
        return showFeed(alias, null, selector, format);
    }

    public String showFeed(String alias, String year, String selector, String format) {

        return "notyet";
//
//        //{year: (/.*)?
//        //,
//        if (year == null) {
//            year = "";
//        } else if (year.startsWith("/")) {
//            year = year.substring(1);
//        }
//
//        ShowDetailed show = mapper.map(db.getCollection("show").findOne(aliasOrId(alias)), ShowDetailed.class);
//
//        Date end;
//        Date start;
//        if ("".equals(year)) {
//            end = getNow();
//            //six monthes
//            start = new Date();
//            start.setTime(end.getTime() - (long) 60 * 24 * 30 * 6 * 60 * 1000);
//        } else {
//            int yearInt = Integer.parseInt(year);
//            start = new Date(yearInt - 1900, 0, 1);
//            end = new Date(yearInt - 1900 + 1, 0, 1);
//        }
//
//        List<EpisodeData> episodeData = episodeUtil.getEpisodeData("" + show.getId(), start, end);
//        Collections.sort(episodeData,
//            (episodeData1, episodeData2) -> episodeData2.getPlannedFrom().compareTo(
//                episodeData1.getPlannedFrom()));
//
//
//        Feed feed = feedRenderer.generateFeed(episodeData, "urn:radio-tilos-hu:show." + alias, "show" + "-" + alias + "-" + selector, format, false, getThumbnail(alias));
//
//        //generate header
//
//        feed.setTitle(show.getName() + " [Tilos Rádió podcast]");
//        feed.setSubtitle(show.getDefinition());
//        feed.setITunesSummary(show.getDescription());
//
//        feed.setUpdated(new Date());
//        feed.setImage(new Image(getThumbnail(alias)));
//
//        String yearPostfix = ("".equals(year) ? "" : "/" + year);
//
//        Link feedLink = new Link();
//        feedLink.setRel("self");
//        feedLink.setType(new MediaType("application", "atom+xml"));
//        feedLink.setHref(uri(serverUrl + "/feed/show/" + show.getAlias() + yearPostfix));
//
//        feed.getLinks().add(feedLink);
//        feed.setId(uri("https://tilos.hu/show/" + show.getAlias() + yearPostfix));
//
//        feed.addAnyOther(new net.anzix.jaxrs.atom.itunes.Category("Society & Culture"));
//        feed.addAnyOther(new Explicit());
//
//        return feed;


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
    public String getThumbnail() {
        return "https://tilos.hu/upload/episode/tilos-radio.jpg";
    }
    public String getThumbnail(String alias) {

        String jpg = "https://tilos.hu/upload/episode-new/" + alias + ".jpg";
        String png = "https://tilos.hu/upload/episode-new/" + alias + ".png";
        String defaultUrl = "https://tilos.hu/upload/episode/tilos-radio.jpg";

        if (imageExists(jpg)) { return jpg; }
        if (imageExists(png)) { return png; }

        return defaultUrl;
    }

    private boolean imageExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String rss2() {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");

        feed.setTitle("Sample Feed (created with ROME)");
        feed.setLink("http://rome.dev.java.net");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;


        entry = new SyndEntryImpl();
        entry.setTitle("ROME v1.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
        try {
            entry.setPublishedDate(dateFormatter.parse("2004-05-23"));
        } catch (ParseException e) {

        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of ROME");
        entry.setDescription(description);

        ArrayList modules = new ArrayList();
        EntryInformation e = new EntryInformationImpl();
        e.setDuration( new Duration( 10000 ) );
        e.setExplicit(false);
        modules.add( e );
        entry.setModules( modules );


        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v3.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
        try {
        entry.setPublishedDate(dateFormatter.parse("2004-05-23"));
        } catch (ParseException e2) {

        }
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"+
                "<p>For details check the <a href=\"https://rometools.jira.com/wiki/display/ROME/Change+Log#ChangeLog-Changesmadefromv0.3tov0.4\">Changes Log</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);

        SyndFeedOutput output = new SyndFeedOutput();
        try {
            return output.outputString(feed);
        } catch (Exception e3) {
            return "Error generating feed";
        }
    }

    private String outputFeed(SyndFeed feed) {

        SyndFeedOutput output = new SyndFeedOutput();
        try {
            return output.outputString(feed);
        } catch (FeedException e) {
            return e.getMessage();
        }
    }

}
