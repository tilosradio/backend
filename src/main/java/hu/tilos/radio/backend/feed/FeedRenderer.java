package hu.tilos.radio.backend.feed;


import com.rometools.modules.itunes.EntryInformation;
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.modules.itunes.FeedInformationImpl;
import com.rometools.modules.itunes.types.Duration;
import com.rometools.rome.feed.synd.*;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import net.anzix.jaxrs.atom.*;
import net.anzix.jaxrs.atom.itunes.Author;
import net.anzix.jaxrs.atom.itunes.Image;

import com.rometools.rome.feed.rss.Channel;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
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

        Feed oldfeed = new Feed();

        oldfeed.setLang("hu");
        feed.setLanguage("hu");

        oldfeed.addAnyOther(new net.anzix.jaxrs.atom.itunes.Owner("Tilos Radio", "info@tilos.hu"));
        oldfeed.getAnyOther().add(new Author("Tilos Radio"));

        feed.setAuthor(FeedService.DEFAULT_OWNER);

        try {
            oldfeed.setId(new URI(id));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        try {


            Person p = new Person();
            p.setEmail("info@tilos.hu");
            p.setName("Tilos Rádió");

            List<Person> authors = new ArrayList();
            authors.add(p);

            List entries = new ArrayList();

            for (EpisodeData episode : episodeData) {
                try {

                    SyndEntry entry = new SyndEntryImpl();

                    List entryModules = entry.getModules();
                    EntryInformation iTunes = new EntryInformationImpl();


                    Entry olde = new Entry();
                    String prefix = prefixedWithShowName ? episode.getShow().getName() + ": " : "";


                    if (episode.getText() != null) {
                        olde.setTitle(prefix + episode.getText().getTitle());

                        String content = episode.getText().getFormatted();

                        olde.setSummary(new Summary("html", content));

                        entry.setTitle(prefix + episode.getText().getTitle());

                        if (content != null) {
                            SyndContent description = new SyndContentImpl();
                            description.setType("text/html");
                            description.setValue(content);
                            entry.setDescription(description);

                            iTunes.setSummary((Jsoup.parse(content).text()));
                        }

                    } else {
                        olde.setTitle(prefix + YYYY_DOT_MM_DOT_DD.format(episode.getPlannedFrom()) + " " + "adásnapló");
                        olde.setSummary(new Summary("adás archívum"));

                        entry.setTitle(prefix + YYYY_DOT_MM_DOT_DD.format(episode.getPlannedFrom()) + " " + "adásnapló");
                        SyndContent description = new SyndContentImpl();
                        description.setType("text/plain");
                        description.setValue("adás archívum");
                        entry.setDescription(description);
                        iTunes.setSummary("adás archívum");
                    }



                    olde.setITunesDuration(
                        (episode.getRealTo().getTime() - episode.getRealFrom()
                            .getTime()) / 1000);


                    iTunes.setDuration(new Duration((episode.getRealTo().getTime() - episode.getRealFrom()
                            .getTime()) / 1000));

                    olde.setPublished(episode.getRealTo());
                    entry.setPublishedDate(episode.getRealTo());
                    olde.setUpdated(episode.getRealTo());
                    entry.setUpdatedDate(episode.getRealTo());
                    olde.setImage(new Image(episode.getThumbnail(defaultThumbnail)));
                    iTunes.setImageUri(episode.getThumbnail(defaultThumbnail));

                    URL url = new URL(serverUrl + "/episode/" + episode.getShow().getAlias() + "/" + YYYY_PER_MM_PER_DD.format(olde.getPublished()));

                    olde.setId(url.toURI());
                    entry.setUri(url.toURI().toString());

                    Link alternate = new Link();
                    alternate.setRel("alternate");
                    alternate.setType(MediaType.TEXT_HTML_TYPE);
                    alternate.setHref(url.toURI());
                    olde.getLinks().add(alternate);

                    Link sound = new Link();
                    sound.setType(new MediaType("audio", "mpeg"));
                    sound.setRel("enclosure");
                    sound.setHref(new URI(createDownloadURI(episode, selector, format)));
                    olde.getLinks().add(sound);

                    ArrayList<SyndEnclosure> enclosures = new ArrayList();
                    SyndEnclosure enclosure = new SyndEnclosureImpl();
                    enclosure.setUrl(createDownloadURI(episode, selector, format));
                    enclosure.setType("audio/mpeg");
                    // FIXME: should get the actual length of the file
                    // enclosure.setLength(123);
                    enclosures.add(enclosure);
                    entry.setEnclosures(enclosures);

                    olde.getAuthors().addAll(authors);

                    oldfeed.getEntries().add(olde);

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
