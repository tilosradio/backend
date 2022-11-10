package hu.tilos.radio.backend.feed;


import com.rometools.modules.itunes.EntryInformation;
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.types.Duration;
import com.rometools.rome.feed.synd.*;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility to create Feed object from Episode list.
 */
@Service
public class FeedRenderer {

    private static final SimpleDateFormat YYYY_DOT_MM_DOT_DD = DateFormatUtil.create("yyyy'.'MM'.'dd");

    private static final SimpleDateFormat YYYY_PER_MM_PER_DD = DateFormatUtil.create("yyyy'/'MM'/'dd");

    private static final SimpleDateFormat YYYYMMDD = DateFormatUtil.create("yyyyMMdd");

    private static final SimpleDateFormat HHMMSS = DateFormatUtil.create("HHmmss");

    @Value("${server.url}")
    private String serverUrl;

    public static String createDownloadURI(EpisodeData episode, String selector, String format) {
        return "https://archive.tilos.hu/mp3/tilos-" +
                YYYYMMDD.format(episode.getRealFrom()) + "-" +
                HHMMSS.format(episode.getRealFrom()) + "-" +
                HHMMSS.format(episode.getRealTo()) + "." + format + (selector == null ? "" : "?s=" + selector);
    }

    private static Date dateFromEpoch(long realTo) {
        Date d = new Date();
        d.setTime(realTo);
        return d;
    }

    public SyndFeed generateFeed(List<EpisodeData> episodeData, String id, String format) {
        return generateFeed(episodeData, id, null, format, false);
    }

    public SyndFeed generateFeed(List<EpisodeData> episodeData, String id, String selector, String format, boolean prefixedWithShowName) {
        FeedService fs = new FeedService();
        return generateFeed(episodeData, id, selector, format, prefixedWithShowName, fs.getThumbnail());
    }

    public SyndFeed generateFeed(List<EpisodeData> episodeData, String id, String selector, String format, boolean prefixedWithShowName, String defaultThumbnail) {

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setLanguage("hu");
        feed.setAuthor(FeedService.DEFAULT_EMAIL);

        try {

            List entries = new ArrayList();

            for (EpisodeData episode : episodeData) {
                try {

                    SyndEntry entry = new SyndEntryImpl();

                    List entryModules = entry.getModules();
                    EntryInformation iTunes = new EntryInformationImpl();
                    String prefix = prefixedWithShowName ? episode.getShow().getName() + ": " : "";

                    if (episode.getText() != null) {
                        String content = episode.getText().getFormatted();

                        if (episode.getText().getTitle() != null &&  episode.getText().getTitle() != "null" && !episode.getText().getTitle().isEmpty()) {
                            entry.setTitle(prefix + episode.getText().getTitle());
                        } else {
                            entry.setTitle(prefix + YYYY_DOT_MM_DOT_DD.format(episode.getPlannedFrom()) + " " + "adásnapló");
                        }

                        if (content != null) {
                            SyndContent description = new SyndContentImpl();
                            description.setType("text/html");
                            description.setValue(content);
                            entry.setDescription(description);
                            iTunes.setSummary((Jsoup.parse(content).text()));
                        }
                    } else {
                        entry.setTitle(prefix + YYYY_DOT_MM_DOT_DD.format(episode.getPlannedFrom()) + " " + "adásnapló");
                        SyndContent description = new SyndContentImpl();
                        description.setType("text/plain");
                        description.setValue("adás archívum");
                        entry.setDescription(description);
                        iTunes.setSummary("adás archívum");
                    }

                    iTunes.setDuration(new Duration(episode.getRealTo().getTime() - episode.getRealFrom()
                            .getTime()));

                    entry.setPublishedDate(episode.getRealTo());
                    entry.setUpdatedDate(episode.getRealTo());
                    iTunes.setImageUri(episode.getThumbnail(defaultThumbnail));

                    URL url = new URL(serverUrl + "/episode/" + episode.getShow().getAlias() + "/" + YYYY_PER_MM_PER_DD.format(entry.getPublishedDate()));

                    entry.setUri(url.toURI().toString());
                    entry.setLink(url.toURI().toString());

                    ArrayList<SyndEnclosure> enclosures = new ArrayList();
                    SyndEnclosure enclosure = new SyndEnclosureImpl();
                    enclosure.setUrl(createDownloadURI(episode, selector, format));
                    enclosure.setType("audio/mpeg");
                    // FIXME: should get the actual length of the file
                    // enclosure.setLength(123);
                    enclosures.add(enclosure);
                    entry.setEnclosures(enclosures);
                    iTunes.setAuthor(FeedService.DEFAULT_OWNER);
                    entryModules.add( iTunes );
                    entry.setModules( entryModules );

                    entries.add(entry);

                } catch (MalformedURLException e1) {
                    throw new RuntimeException(e1);
                } catch (URISyntaxException e1) {
                    throw new RuntimeException(e1);
                }
            }

            feed.setEntries(entries);

        } catch (Exception ex) {
            ex.printStackTrace();
            //TODO
        }

        return feed;
    }
}
