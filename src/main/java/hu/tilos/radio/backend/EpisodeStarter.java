package hu.tilos.radio.backend;

import akka.actor.ActorSystem;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.bookmark.BookmarkService;
import hu.tilos.radio.backend.bookmark.BookmarkToSave;
import hu.tilos.radio.backend.bus.MessageBus;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.EpisodeToSave;
import hu.tilos.radio.backend.feed.FeedService;
import hu.tilos.radio.backend.m3u.M3uService;
import hu.tilos.radio.backend.spark.FeedTransformer;
import hu.tilos.radio.backend.spark.GuiceConfigurationListener;
import hu.tilos.radio.backend.spark.JsonTransformer;
import hu.tilos.radio.backend.spark.SparkDefaults;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import spark.Response;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import static spark.Spark.*;

public class EpisodeStarter {

    private static final Logger LOG = LoggerFactory.getLogger(EpisodeStarter.class);

    private Gson gson;

    static Injector injector;

    private FiniteDuration timeout;

    @Inject
    EpisodeService episodeService;

    @Inject
    BookmarkService bookmarkService;

    @Inject
    FeedService feedService;

    @Inject
    M3uService m3uService;

    @Inject
    @Configuration(name = "port.episode")
    private int portEpisode;

    MessageBus bus;

    public static void main(String[] args) {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DB.class).toProvider(MongoProducer.class);
                bind(DozerBeanMapper.class).toProvider(DozerFactory.class).asEagerSingleton();
                bind(Validator.class).toProvider(ValidatorProducer.class);
                bindListener(Matchers.any(), new GuiceConfigurationListener());
                bind(Integer.class).toInstance(1);

            }
        });

        injector.getInstance(EpisodeStarter.class).run();

    }

    private void run() {
        ActorSystem system = ActorSystem.create("TilosBus");
        bus = new MessageBus(system, injector);

        timeout = Duration.create(5, "seconds");

        LOG.info("Starting new deployment");

        SparkDefaults spark = new SparkDefaults(portEpisode, injector);

        JsonTransformer jsonResponse = spark.getJsonTransformer();


        post("/api/v1/episode/:id/bookmark", spark.authorized(Role.ADMIN, (req, res, session) ->
                bookmarkService.create(session, req.params("id"), gson.fromJson(req.body(), BookmarkToSave.class))), jsonResponse);

        get("/api/v1/episode", (req, res) ->
                episodeService.listEpisodes(spark.longParam(req, "start"), spark.longParam(req, "end")), jsonResponse);
        get("/api/v1/episode/next", (req, res) ->
                episodeService.next(), jsonResponse);
        get("/api/v1/episode/last", (req, res) ->
                episodeService.last(), jsonResponse);
        get("/api/v1/episode/lastWeek", (req, res) ->
                episodeService.lastWeek(), jsonResponse);
        get("/api/v1/episode/now", (req, res) ->
                episodeService.now(), jsonResponse);
        get("/api/v1/episode/:id", (req, res) ->
                episodeService.get(req.params("id")), jsonResponse);
        get("/api/v1/episode/:show/:year/:month/:day", (req, res) ->
                        episodeService.getByDate(req.params("show"),
                                Integer.parseInt(req.params("year")),
                                Integer.parseInt(req.params("month")),
                                Integer.parseInt(req.params("day"))),
                jsonResponse);
        post("/api/v1/episode",
                spark.authorized(Role.AUTHOR, (req, res, session) ->
                        episodeService.create(gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);
        put("/api/v1/episode/:id",
                spark.authorized(Role.AUTHOR, (req, res, session) ->
                        episodeService.update(req.params("id"), gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);


        get("/feed/weekly", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.weeklyFeed();
        }, new FeedTransformer());

        get("/feed/podcast", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.tilosFeed(null);
        }, new FeedTransformer());

        get("/feed/podcast/:type", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.tilosFeed(req.params("type"));
        }, new FeedTransformer());


        get("/feed/weekly/:type", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.weeklyFeed(req.params("type"));
        }, new FeedTransformer());
        get("/feed/show/itunes/:alias", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), null);
        }, new FeedTransformer());
        get("/feed/show/:alias", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), null);
        }, new FeedTransformer());
        get("/feed/show/:alias/:year", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), req.params("year"));
        }, new FeedTransformer());


        get("/api/v1/m3u/lastweek", (req, res) -> {
            return asM3u(res, m3uService.lastWeek(req.queryParams("stream"), req.queryParams("type")));
        });


    }

    private Object asM3u(Response res, String output) throws Exception {
        res.type("audio/x-mpegurl; charset=iso-8859-2");
        try (OutputStreamWriter writer = new OutputStreamWriter(res.raw().getOutputStream(), Charset.forName("ISO-8859-2"))) {
            writer.write(output);
        }
        return null;
    }

}
