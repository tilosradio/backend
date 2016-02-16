package hu.tilos.radio.backend;

import hu.tilos.radio.backend.feed.FeedService;
import net.anzix.jaxrs.atom.Feed;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class FeedController {

    @Inject
    FeedService feedService;

    @RequestMapping(value = "/feed/weekly", produces = "application/atom+xml")
    public Feed weekly() {
        return feedService.weeklyFeed();
    }

    @RequestMapping(value = "/feed/weekly/{type}", produces = "application/atom+xml")
    public Feed weeklySpecial(@PathVariable String type) {
        return feedService.weeklyFeed(type);
    }

    @RequestMapping(value = "/feed/podcast", produces = "application/atom+xml")
    public Feed podcast() {
        return feedService.tilosFeed(null);
    }

    @RequestMapping(value = "/feed/podcast/{type}", produces = "application/atom+xml")
    public Feed podcastSpecial(@PathVariable String type) {
        return feedService.tilosFeed(type);
    }

    @RequestMapping(value = "/feed/show/{show}", produces = "application/atom+xml")
    public Feed showFeed(@PathVariable String show) {
        return feedService.showFeed(show, "normal");
    }

    @RequestMapping(value = "/feed/show/itunes/{show}", produces = "application/atom+xml")
    public Feed showFeedItunes(@PathVariable String show) {
        return feedService.showFeed(show, "itunes");
    }


    @RequestMapping(value = "/feed/show/{show}/{year}", produces = "application/atom+xml")
    public Feed showFeedYearly(@PathVariable String show, @PathVariable String year) {
        return feedService.showFeed(show, year, "show-feed");
    }

}
