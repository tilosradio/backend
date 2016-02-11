package hu.tilos.radio.backend.stat;

import com.github.fakemongo.junit.FongoRule;

import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.converters.FairEnoughHtmlSanitizer;
import hu.tilos.radio.backend.converters.HTMLSanitizer;
import hu.tilos.radio.backend.episode.EpisodeRepository;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.util.*;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EpisodeService.class, EpisodeUtil.class, TagUtil.class,
        StatService.class, PersistentEpisodeProvider.class, ScheduledEpisodeProvider.class, ExtraEpisodeProvider.class,
        Merger.class, ShowCache.class, TextConverter.class, HTMLSanitizer.class, FairEnoughHtmlSanitizer.class,
        EpisodeRepository.class, FongoCreator.class, DozerFactory.class})
public class StatServiceTest {


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
        //TODO handle time zone
        //Assert.assertEquals(1, listenerSTat.size());

    }
}