package hu.tilos.radio.backend.mix;

import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.author.AuthorDetailed;
import hu.tilos.radio.backend.author.AuthorListElement;
import hu.tilos.radio.backend.author.AuthorToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class MixController {

    @Inject
    private MixService mixService;

    @RequestMapping(value = "/api/v1/mix")
    public List<Mix> list(@RequestParam(name = "category", required = false) String category, @RequestParam(name = "show", required = false) String show) {
        return mixService.list(category, show);
    }

    @RequestMapping(value = "/api/v1/mix/{id}")
    public Mix update(@PathVariable String id) {
        return mixService.get(id);
    }

}
