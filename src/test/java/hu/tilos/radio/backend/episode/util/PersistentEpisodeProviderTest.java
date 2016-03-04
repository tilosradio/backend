package hu.tilos.radio.backend.episode.util;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.EpisodeRepositoryMockFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.converters.FairEnoughHtmlSanitizer;
import hu.tilos.radio.backend.converters.HTMLSanitizer;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.stat.StatService;
import hu.tilos.radio.backend.tag.TagUtil;
import hu.tilos.radio.backend.util.ShowCache;
import hu.tilos.radio.backend.util.TextConverter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EpisodeService.class, EpisodeUtil.class, TagUtil.class,
        StatService.class, PersistentEpisodeProvider.class, ScheduledEpisodeProvider.class, ExtraEpisodeProvider.class,
        Merger.class, ShowCache.class, TextConverter.class, HTMLSanitizer.class, FairEnoughHtmlSanitizer.class,
        EpisodeRepositoryMockFactory.class, FongoCreator.class, DozerFactory.class})
public class PersistentEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Inject
    PersistentEpisodeProvider p;

    @Inject
    private FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void testListEpisode() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "episode", "episode-episode1.json", showId);
        loadTo(fongoRule, "episode", "episode-episode2.json", showId);


        //when
        List<EpisodeData> episodes = p.listEpisode(showId, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
        Assert.assertNotNull(episodes.get(1).getShow());
        Assert.assertNotNull(episodes.get(1).getText());
        Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());


    }
}