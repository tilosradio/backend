package hu.tilos.radio.backend.episode.util;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import com.mongodb.DB;
import static hu.tilos.radio.backend.MongoTestUtil.dropCollection;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import hu.tilos.radio.backend.episode.EpisodeData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersistentEpisodeProviderTest {

  private static final SimpleDateFormat SDF =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Inject
  PersistentEpisodeProvider p;

  @Inject
  DB mongodb;

  @Test
  public void testListEpisode() throws Exception {
    //given
    dropCollection(mongodb,"show");
    dropCollection(mongodb,"episode");

    String showId = loadTo(mongodb, "show", "show-3utas.json");
    loadTo(mongodb, "episode", "episode-episode1.json", showId);
    loadTo(mongodb, "episode", "episode-episode2.json", showId);

    //when
    List<EpisodeData> episodes =
        p.listEpisode(showId, SDF.parse("2014-04-03 12:00:00"),
            SDF.parse("2014-05-03 12:00:00"));

    //then
    Assert.assertEquals(2, episodes.size());
    Assert.assertNotNull(episodes.get(1).getShow());
    Assert.assertNotNull(episodes.get(1).getText());
    Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());

  }
}