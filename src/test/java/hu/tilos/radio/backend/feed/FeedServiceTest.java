package hu.tilos.radio.backend.feed;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import net.anzix.jaxrs.atom.Feed;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


public class FeedServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

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
        Feed feed = (Feed) feedController.feed("3utas", null);

        //then
        JAXBContext jaxbc = JAXBContext.newInstance(Feed.class);
        Marshaller marshaller = jaxbc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(feed, System.out);

    }
}