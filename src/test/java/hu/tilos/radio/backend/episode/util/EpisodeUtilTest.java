package hu.tilos.radio.backend.episode.util;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import static hu.tilos.radio.backend.MongoTestUtil.dropCollection;
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
public class EpisodeUtilTest {
  private static final SimpleDateFormat SDF =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Inject
  EpisodeUtil episodeUtil;

  @Inject
  DB mongodb;

  @Test
  public void testListEpisode() throws Exception {
    //given
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "episode");
    String show3utas = loadTo(mongodb, "show", "show-3utas.json");
    String showVendeglo = loadTo(mongodb, "show", "show-vendeglo.json");
    loadTo(mongodb, "episode", "episode-episode1.json", show3utas);
    loadTo(mongodb, "episode", "episode-episode2.json", show3utas);
    loadTo(mongodb, "episode", "episode-vendeglo-extra.json", showVendeglo);

    //when
    List<EpisodeData> episodes = episodeUtil
        .getEpisodeData(show3utas, SDF.parse("2014-04-03 12:00:00"),
            SDF.parse("2014-05-03 12:00:00"));

    //then
    episodes.stream().forEach(e -> System.out.println(e));
    Assert.assertEquals(5, episodes.size());
    Assert.assertNotNull(episodes.get(1).getShow());
    Assert.assertNotNull(episodes.get(1).getText());
    Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());
    Assert.assertEquals(6,
        mongodb.getCollection("episode").find().count());

  }
}