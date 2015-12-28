package hu.tilos.radio.backend.stat;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


public class StatServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    StatService controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void testGetListenerStat() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmm");
        //given
        loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "stat_icecast", "stat2.json");
        loadTo(fongoRule, "stat_icecast", "stat1.json");
        Date from = sdf.parse("20140419 0800");
        Date to = sdf.parse("20140419 1000");

        //when

        List<ListenerStatWithEpisode> listenerSTat = controller.getListenerStatOfEpisodes(from.getTime(), to.getTime());

        //then

        Assert.assertEquals(1, listenerSTat.size());

    }
}