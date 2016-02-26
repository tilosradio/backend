package hu.tilos.radio.backend;

import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.bookmark.BookmarkService;
import hu.tilos.radio.backend.bookmark.BookmarkToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.EpisodeToSave;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private BookmarkService bookmarkService;

    @RequestMapping("/api/v1/episode")
    public List<EpisodeData> list(@RequestParam(required = false) String id,
                                  @RequestParam(required = false, defaultValue = "0") Long start,
                                  @RequestParam(required = false, defaultValue = "0") Long end) {
        if (id == null) {
            return episodeService.listEpisodes(start, end);
        } else {
            return episodeService.listEpisodes(id, start, end);
        }

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

    @RequestMapping("/api/v1/episode/report")
    @ResponseBody
    public String csvReport() {
        return episodeService.previousWeekAsCsv();
    }

    @RequestMapping("/api/v1/episode/now")
    public EpisodeData now() {
        return episodeService.now();
    }

    @RequestMapping("/api/v1/episode/{id}")
    public EpisodeData get(@PathVariable String id) {
        return episodeService.get(id);
    }


    @RequestMapping("/api/v1/episode/{show}/{year}/{month}/{day}")
    public EpisodeData getByDate(@PathVariable String show, @PathVariable int year, @PathVariable int month, @PathVariable int day) {
        return episodeService.getByDate(show, year, month, day);
    }


    @RequestMapping("/api/v1/show/{show}/episodes")
    public List<EpisodeData> getByDate(@PathVariable String show, @RequestParam long start, @RequestParam long end) {
        return episodeService.listEpisodes(show, start, end);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/episode/{id}", method = RequestMethod.PUT)
    public UpdateResponse update(@PathVariable String id, @RequestBody EpisodeToSave episode) {
        return episodeService.update(id, episode);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/episode", method = RequestMethod.POST)
    public CreateResponse create(@RequestBody EpisodeToSave episode) {
        return episodeService.create(episode);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/episode/{id}/bookmark", method = RequestMethod.POST)
    public CreateResponse createBookmark(@PathVariable String id, @RequestBody BookmarkToSave bookmark) {
        return bookmarkService.create(getCurrentSession(), id, bookmark);
    }

    public Session getCurrentSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtToken) {
            JwtToken authToken = (JwtToken) auth;
            return authToken.getSession();
        }
        return null;
    }


}
