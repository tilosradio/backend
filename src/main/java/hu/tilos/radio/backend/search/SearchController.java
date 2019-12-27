package hu.tilos.radio.backend.search;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class SearchController {

    @Inject
    SearchService searchService;

    @RequestMapping(value = "/api/v1/search/query", method = RequestMethod.GET)
    public SearchResponse search(@RequestParam String q) {
        try {
            return searchService.search(q);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/api/v1/search/index", method = RequestMethod.GET)
    public String index() {
        return searchService.index();
    }

}
