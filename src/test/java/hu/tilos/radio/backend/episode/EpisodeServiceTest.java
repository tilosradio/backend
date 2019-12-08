package hu.tilos.radio.backend.episode;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import static hu.tilos.radio.backend.MongoTestUtil.dropCollection;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;
import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EpisodeServiceTest {

  @Inject
  EpisodeService controller;

  @Inject
  ApplicationContext applicationContext;

  @Inject
  DozerBeanMapper mapper;

  @Inject
  DB mongodb;

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void testGetWithStat() throws Exception {

    //given
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    loadTo(mongodb, "episode", "episode-episode2.json", showId);
    loadTo(mongodb, "stat_icecast", "stat-for-episode2.json", showId);

    //when
    EpisodeData episode = controller.get("2");

    //then
    Assert.assertNotNull(episode.getText());
    Assert.assertEquals("Jo musor", episode.getText().getTitle());
    Assert.assertNotNull(episode.getStatListeners());
    Assert.assertEquals(105, episode.getStatListeners().getMax());

    //TODO test should be timezone independent
    //Assert.assertEquals("http://tilos.hu/mp3/tilos-20140411-100000-120000
    // .m3u", episode.getM3uUrl());
  }

  @Test
  public void testGetWithoutStat() throws Exception {

    dropCollection(mongodb, "stat_icecast");
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "episode");
    //given
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    loadTo(mongodb, "episode", "episode-episode2.json", showId);

    //when
    EpisodeData episode = controller.get("2");

    //then
    Assert.assertNotNull(episode.getText());
    Assert.assertEquals("Jo musor", episode.getText().getTitle());
    Assert.assertNotNull(episode.getStatListeners());
    Assert.assertEquals(0, episode.getStatListeners().getMax());


  }

  @Test
  public void testGetByDate() throws Exception {
    //given
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    loadTo(mongodb, "episode", "episode-episode2.json", showId);

    //when
    EpisodeData episode = controller.getByDate("3utas", 2014, 04, 11);

    //then
    Assert.assertNotNull(episode.getText());
    Assert.assertEquals("Jo musor", episode.getText().getTitle());
  }

  @Test
  public void testGetByDateWithBookmarks() throws Exception {
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "user");
    dropCollection(mongodb, "episode");
    //given
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    String userId = loadTo(mongodb, "user", "user-noauthor.json");
    loadTo(mongodb, "episode", "episode-withbookmark.json", userId,
        showId);

    //when
    EpisodeData episode = controller.getByDate("3utas", 2014, 04, 04);

    //then
    Assert.assertEquals(1, episode.getBookmarks().size());
    Assert.assertEquals("Remek interjú a három fejű sárkánnyal",
        episode.getText().getTitle());
  }

  @Test
  public void testCreateEpisode() throws Exception {
    //given
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "episode");
    String showId = loadTo(mongodb, "show", "show-3utas.json");

    EpisodeToSave episode = new EpisodeToSave();
    episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
    episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

    ShowSimple simple = new ShowSimple();
    simple.setId(showId);
    episode.setShow(simple);

    TextData td = new TextData();
    td.setTitle("Title");
    td.setContent("ahoj #teg ahoj");
    episode.setText(td);

    //when
    CreateResponse createResponse = controller.create(episode);

    //then
    DBObject mongoEpisode =
        mongodb.getCollection("episode").findOne();
    System.out.println(JSON.serialize(mongoEpisode));
    SimpleDateFormat sdf =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss .SSS 'Z' ");

  }

  @Test
  public void testUpdateEpisode() throws Exception {
    //given
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "episode");
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    String episodeId =
        loadTo(mongodb, "episode", "episode-episode1.json", showId);

    EpisodeToSave episode =
        mapper.map(controller.get(episodeId), EpisodeToSave.class);
    episode.setText(new TextData());
    episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
    episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

    episode.getText()
        .setContent("ez jobb #kukac de a harom nincs @szemely is van");

    //when
    UpdateResponse createResponse = controller.update(episodeId, episode);

    //then
    DBObject mEpisode = mongodb.getCollection("episode")
        .findOne();
    DBObject text = (DBObject) mEpisode.get("text");
    Assert.assertNotNull(text);
    Assert.assertEquals("ez jobb #kukac de a harom nincs @szemely is van",
        text.get("content"));

  }

  @Test
  public void testUpdateEpisodeWithStats() throws Exception {
    dropCollection(mongodb, "show");
    dropCollection(mongodb, "episode");
    dropCollection(mongodb, "stat_icecasr");
    //given
    String showId = loadTo(mongodb, "show", "show-3utas.json");
    String episodeId =
        loadTo(mongodb, "episode", "episode-episode1.json", showId);
    loadTo(mongodb, "stat_icecast", "stat1.json");
    loadTo(mongodb, "stat_icecast", "stat2.json");

    EpisodeToSave episode =
        mapper.map(controller.get(episodeId), EpisodeToSave.class);
    episode.setText(new TextData());

    episode.getText()
        .setContent("ez jobb #kukac de a harom nincs @szemely is van");

    //when
    UpdateResponse createResponse = controller.update(episodeId, episode);

    //then
    DBObject mEpisode = mongodb.getCollection("episode")
        .findOne();
    DBObject listeners = (DBObject) mEpisode.get("statListeners");
    Assert.assertEquals(126, listeners.get("max"));

  }
}