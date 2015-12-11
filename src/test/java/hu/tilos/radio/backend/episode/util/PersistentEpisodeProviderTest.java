package hu.tilos.radio.backend.episode.util;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeGuiceRunner;
import hu.tilos.radio.backend.episode.util.PersistentEpisodeProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

public class PersistentEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Rule
    public GuiceRunner guice = new EpisodeGuiceRunner(this);

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