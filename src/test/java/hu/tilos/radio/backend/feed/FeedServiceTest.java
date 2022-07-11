package hu.tilos.radio.backend.feed;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import net.anzix.jaxrs.atom.Feed;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FeedServiceTest {

  @Inject
  FeedService feedController;

  @Inject
  DB mongo;

  @Test
  public void testFeed() throws Exception {
//    //given
//    loadTo(mongo, "show", "show-3utas.json");
//
//    feedController.setServerUrl("https://tilos.hu");
//
//    //when
//    Feed feed = (Feed) feedController.showFeed("3utas", null, "show-feed");
//
//    //then
//    JAXBContext jaxbc = JAXBContext.newInstance(Feed.class);
//    Marshaller marshaller = jaxbc.createMarshaller();
//    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//    marshaller.marshal(feed, System.out);

  }
}