package hu.tilos.radio.backend.feed;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.EpisodeRepositoryMockFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.converters.FairEnoughHtmlSanitizer;
import hu.tilos.radio.backend.converters.HTMLSanitizer;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.util.*;
import hu.tilos.radio.backend.stat.StatService;
import hu.tilos.radio.backend.tag.TagUtil;
import hu.tilos.radio.backend.util.ShowCache;
import hu.tilos.radio.backend.util.TextConverter;
import net.anzix.jaxrs.atom.Feed;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EpisodeService.class, EpisodeUtil.class, TagUtil.class,
        StatService.class, PersistentEpisodeProvider.class, ScheduledEpisodeProvider.class, ExtraEpisodeProvider.class,
        Merger.class, ShowCache.class, TextConverter.class, HTMLSanitizer.class, FairEnoughHtmlSanitizer.class,
        EpisodeRepositoryMockFactory.class, FongoCreator.class, DozerFactory.class, FeedService.class, FeedRenderer.class})
public class FeedServiceTest {

    @Inject
    FeedService feedController;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testFeed() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");

        feedController.setServerUrl("http://tilos.hu");


        //when
        Feed feed = (Feed) feedController.showFeed("3utas", null, "show-feed");

        //then
        JAXBContext jaxbc = JAXBContext.newInstance(Feed.class);
        Marshaller marshaller = jaxbc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(feed, System.out);

    }
}