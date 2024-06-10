package hu.tilos.radio.backend.recommendation;

import hu.tilos.radio.backend.auth.UserService;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class RecommendationController {

    @Inject
    private RecommendationService recommendationService;

    @Inject
    private UserService userService;

    @RequestMapping(value = "/api/v1/recommendation", method = RequestMethod.GET)
    public List<RecommendationSimple> list(@RequestParam(name = "type", defaultValue = "all", required = false) String type) {
        return recommendationService.list(type);
    }

    @RequestMapping(value = "/api/v1/recommendation/{id}", method = RequestMethod.GET)
    public RecommendationData get(@PathVariable String id) {
        return recommendationService.get(id);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/recommendation", method = RequestMethod.POST)
    public CreateResponse create(@RequestBody @Validated RecommendationToSave recommendation) {
        return recommendationService.create(recommendation, userService.getUserById(userService.getCurrentUser().getId()));
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/recommendation/{id}", method = RequestMethod.PUT)
    public UpdateResponse update(@PathVariable String id, @RequestBody @Validated RecommendationToSave recommendation) {
        return recommendationService.update(id, recommendation, userService.getUserById(userService.getCurrentUser().getId()));
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/recommendation/{id}", method = RequestMethod.DELETE)
    public OkResponse delete(@PathVariable String id) {
        return recommendationService.delete(id, userService.getUserById(userService.getCurrentUser().getId()));
    }
}
