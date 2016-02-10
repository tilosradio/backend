package hu.tilos.radio.backend;

import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @RequestMapping("/api/v1/episode")
    public List<EpisodeData> list(@RequestParam(defaultValue = "") String id, @RequestParam long from, @RequestParam long to) {
        if (id.equals("")) {
            id = null;
        }
        return episodeService.listEpisodes(id, from, to);
    }

    @RequestMapping("/api/v1/episode/next")
    public List<EpisodeData> next() {
        return episodeService.next();
    }

    @RequestMapping("/api/v1/episode/last")
    public List<EpisodeData> last() {
        return episodeService.last();
    }

    @RequestMapping("/api/v1/episode/lastWeek")
    public List<EpisodeData> lastWeek() {
        return episodeService.lastWeek();
    }

    @RequestMapping("/api/v1/episode/now")
    public EpisodeData now() {
        return episodeService.now();
    }

    @RequestMapping("/api/v1/episode/{id}")
    public EpisodeData get(@RequestParam String id) {
        return episodeService.get(id);
    }


//    post("/api/v1/episode/:id/bookmark", spark.authorized(Role.ADMIN, (req, res, session) ->
//            bookmarkService.create(session, req.params("id"), gson.fromJson(req.body(), BookmarkToSave.class))), jsonResponse);
//


//    get("/api/v1/episode/:show/:year/:month/:day", (req, res) ->
//            episodeService.getByDate(req.params("show"),
//            Integer.parseInt(req.params("year")),
//            Integer.parseInt(req.params("month")),
//            Integer.parseInt(req.params("day"))),
//    jsonResponse);
//    post("/api/v1/episode",
//         spark.authorized(Role.AUTHOR, (req, res, session) ->
//            episodeService.create(gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);
//    put("/api/v1/episode/:id",
//        spark.authorized(Role.AUTHOR, (req, res, session) ->
//            episodeService.update(req.params("id"), gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);
//
//    get("/api/v1/show/:alias/episodes", (req, res) ->
//            episodeService.listEpisodes(req.params("alias"),
//            Long.valueOf(req.queryParams("start")),
//            Long.valueOf(req.queryParams("end"))
//            ), jsonResponse);

}
