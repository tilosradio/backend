package hu.tilos.radio.backend.episode.util;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import com.github.fakemongo.junit.FongoRule;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import hu.tilos.radio.backend.episode.EpisodeData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ScheduledEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    ScheduledEpisodeProvider p;

    @Inject
    private FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testListEpisode() throws Exception {
        //given
        p.setDb(fongoRule.getDB());
        loadTo(fongoRule, "show", "show-3utas.json");

        //when
        List<EpisodeData> episodes = p.listEpisode("1", SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(3, episodes.size());

    }

    @Test
    public void testListEpisodeWithBase() throws Exception {
        //given
        p.setDb(fongoRule.getDB());
        loadTo(fongoRule, "show", "show-vendeglo.json");

        //when
        List<EpisodeData> episodes = p.listEpisode("3", SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
    }
}