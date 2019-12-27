package hu.tilos.radio.backend.content.page;

import hu.tilos.radio.backend.text.TextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PageController {

    @Autowired
    PageRepository repository;

    @Autowired
    TextConverter textConverter;

    @RequestMapping(value = "/api/v1/text/page/{alias}")
    public Page get(@PathVariable String alias) {
        return render(repository.findByAliasOrId(alias, alias));
    }


    @RequestMapping(value = "/api/v1/text/page")
    public List<Page> list() {
        return repository.findAll().stream().map(a -> {
            a.setContent(null);
            return a;
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/page/{alias}", method = RequestMethod.PUT)
    public void update(@RequestBody Page page) {
        repository.save(page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/page/{alias}", method = RequestMethod.POST)
    public Page create(@PathVariable String alias, @RequestBody Page page) {
        return render(repository.insert(page));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/page/{alias}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String alias) {
        repository.deletePageByAlias(alias);
    }

    public Page render(Page p) {
        p.setFormatted(textConverter.format("markdown", p.getContent()));
        return p;
    }
}
