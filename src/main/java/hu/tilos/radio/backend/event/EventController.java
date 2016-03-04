package hu.tilos.radio.backend.event;

import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.bookmark.BookmarkToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.episode.EpisodeToSave;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/episode/{id}/event", method = RequestMethod.POST)
    public CreateResponse update(@PathVariable String id, @RequestBody Event event) {
        return eventService.insert(id, event);
    }



}
