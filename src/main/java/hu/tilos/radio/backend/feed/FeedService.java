package hu.tilos.radio.backend.feed;

import com.mongodb.DB;
import com.rometools.modules.atom.modules.AtomLinkModule;
import com.rometools.modules.atom.modules.AtomLinkModuleImpl;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.modules.itunes.FeedInformationImpl;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import hu.tilos.radio.backend.data.types.ShowType;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.show.ShowDetailed;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

/**
 * Generate atom showFeed for the shows.
 */
@Service
public class FeedService {

    public static final String DEFAULT_OWNER = "Tilos Rádió";
    public static final String DEFAULT_EMAIL = "info@tilos.hu";
    public static final String DEFAULT_CATEGORY = "Society & Culture";
    public static final String DEFAULT_SITE_LINK = "https://tilos.hu/";


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


        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);

        List<EpisodeData> episodes = filter(episodeUtil.getEpisodeData(null, weekAgo, now), type);

        Collections.sort(episodes, (episodeData, episodeData2) -> episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom()));

        SyndFeed feed = feedRenderer.generateFeed(episodes,
                "urn:radio-tilos-hu:weekly" + (type == null ? "" : "." + type),
                "weekly" + (type == null ? "" : "-" + type),
                format,
                true);



        feed.setTitle("Tilos Rádió heti podcast");
        String description = "Tilos Rádió heti podcast";
        feed.setDescription(description);
        feed.setPublishedDate(new Date());

        String feedUrl = serverUrl + "/feed/weekly";
        if (type != null) {
            feedUrl = feedUrl + "/" + type;
        }

        feed.setLink(serverUrl);
        feed.setUri(feedUrl);

        List modules = feed.getModules();
        modules.add(getAtomLinkModule(feedUrl));
        modules.add(getITunesModule(description, getCategory(), getThumbnail()));
        feed.setModules(modules);

        return outputFeed(feed);
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


        FeedInformation iTunes = new FeedInformationImpl();
        String categoryName = FeedService.DEFAULT_CATEGORY;

        if (type == null) {
            feed.setTitle("Tilos Rádió podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi adásaiból";
            feed.setDescription(description);
        } else if (type.equals("talk")) {
            feed.setTitle("Tilos Rádió szöveges podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi szöveges adásaiból";
            feed.setDescription(description);
        } else if (type.equals("music")) {
            feed.setTitle("Tilos Rádió zenés podcast");
            String description = "Válogatás a Tilos Rádió legutóbbi zenés adásaiból";
            feed.setDescription(description);
            categoryName = "Music";
        }


        String feedUrl = serverUrl + "/feed/podcast";
        if (type != null) {
            feedUrl = feedUrl + "/" + type;
        }

        feed.setLink(serverUrl);
        feed.setUri(feedUrl);

        List modules = feed.getModules();
        modules.add(getAtomLinkModule(feedUrl));
        modules.add(getITunesModule(feed.getDescription(), getCategory(categoryName), getThumbnail()));
        feed.setModules(modules);

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


        //{year: (/.*)?
        //,
        if (year == null) {
            year = "";
        } else if (year.startsWith("/")) {
            year = year.substring(1);
        }

        ShowDetailed show = mapper.map(db.getCollection("show").findOne(aliasOrId(alias)), ShowDetailed.class);

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
        Collections.sort(episodeData,
            (episodeData1, episodeData2) -> episodeData2.getPlannedFrom().compareTo(
                episodeData1.getPlannedFrom()));


        SyndFeed feed = feedRenderer.generateFeed(episodeData, "urn:radio-tilos-hu:show." + alias, "show" + "-" + alias + "-" + selector, format, false, getThumbnail(alias));

        feed.setTitle(show.getName() + " [Tilos Rádió podcast]");
        feed.setDescription(show.getAnyDescription());
        feed.setPublishedDate(new Date());
        String yearPostfix = ("".equals(year) ? "" : "/" + year);

        String feedUrl = serverUrl + "/feed/show/" + show.getAlias() + yearPostfix;
        String siteUrl = serverUrl + "/show/" + show.getAlias();

        feed.setUri(feedUrl);
        feed.setLink(siteUrl);

        List modules = feed.getModules();
        modules.add(getAtomLinkModule(feedUrl));
        modules.add(getITunesModule(show.getDefinition(), getCategory(), getThumbnail(alias)));
        feed.setModules(modules);

        return outputFeed(feed);
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

    private ArrayList<com.rometools.modules.itunes.types.Category> getCategory() {
        return getCategory(FeedService.DEFAULT_CATEGORY);
    }

    private ArrayList<com.rometools.modules.itunes.types.Category> getCategory(String categoryName) {
        ArrayList<com.rometools.modules.itunes.types.Category> categories = new ArrayList();
        com.rometools.modules.itunes.types.Category category = new com.rometools.modules.itunes.types.Category();
        category.setName(categoryName);
        categories.add(category);
        return categories;
    }

    private String outputFeed(SyndFeed feed) {

        SyndFeedOutput output = new SyndFeedOutput();
        try {
            return output.outputString(feed);
        } catch (FeedException e) {
            return e.getMessage();
        }
    }


    private FeedInformation getITunesModule(String description, ArrayList categories, String imageUrl) {
        FeedInformation iTunes = new FeedInformationImpl();

        iTunes.setAuthor(FeedService.DEFAULT_OWNER);
        iTunes.setOwnerName(FeedService.DEFAULT_OWNER);
        iTunes.setOwnerEmailAddress(FeedService.DEFAULT_EMAIL);
        iTunes.setExplicit(false);
        iTunes.setSummary(description);
        iTunes.setCategories(categories);
        iTunes.setImageUri(imageUrl);

        return iTunes;
    }

    private AtomLinkModule getAtomLinkModule(String feedUrl) {
        Link link = new Link();
        link.setRel("self");
        link.setType("application/rss+xml");
        link.setHref(feedUrl);
        AtomLinkModule atom = new AtomLinkModuleImpl();
        atom.setLink(link);
        return atom;
    }
}
