package hu.tilos.radio.backend;

import hu.tilos.radio.backend.feed.FeedService;
import net.anzix.jaxrs.atom.Feed;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class FeedController {

    @Inject
    FeedService feedService;

    @RequestMapping(value = "/feed/weekly", produces = "application/rss+xml")
    public String weekly(@RequestParam(defaultValue = "mp3") String format) {
        return feedService.weeklyFeed(format);
    }

    @RequestMapping(value = "/feed/weekly/{type}", produces = "application/rss+xml")
    public String weeklySpecial(@PathVariable String type, @RequestParam(defaultValue = "mp3") String format) {
        return feedService.weeklyFeed(type, format);
    }

    @RequestMapping(value = "/feed/podcast", produces = "application/rss+xml")
    public String podcast(@RequestParam(defaultValue = "mp3") String format) {
        return feedService.tilosFeed(null, format);
    }

    @RequestMapping(value = "/feed/podcast/{type}", produces = "application/rss+xml")
    public String podcastSpecial(@PathVariable String type, @RequestParam(defaultValue = "mp3") String format) {
        return feedService.tilosFeed(type, format);
    }

    @RequestMapping(value = "/feed/show/{show}", produces = "application/rss+xml")
    public String showFeed(@PathVariable String show, @RequestParam(defaultValue = "mp3") String format) {
        return feedService.showFeed(show, "normal", format);
    }

    @RequestMapping(value = "/feed/show/itunes/{show}", produces = "application/rss+xml")
    public String showFeedItunes(@PathVariable String show) {
        return feedService.showFeed(show, "itunes", "mp3");
    }

    @RequestMapping(value = "/feed/show/{show}/{year}", produces = "application/rss+xml")
    public String showFeedYearly(@PathVariable String show, @PathVariable String year) {
        return feedService.showFeed(show, year, "show-feed");
    }

}
