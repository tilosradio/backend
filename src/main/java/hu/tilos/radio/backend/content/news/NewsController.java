package hu.tilos.radio.backend.content.news;

import hu.tilos.radio.backend.text.TextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@RestController
public class NewsController {

    @Autowired
    NewsRepository repository;

    @Autowired
    TextConverter textConverter;

    @RequestMapping(value = "/api/v1/text/news/{alias}")
    public News get(@PathVariable String alias) {
        return render(repository.findByAliasOrId(alias, alias));
    }


    @RequestMapping(value = "/api/v1/text/news")
    public List<News> list() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(this::render).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/v1/text/news/current")
    public List<News> actual() {
        return repository.findAll( PageRequest.of(0, 10, Sort.Direction.DESC, "created")).getContent().stream()
                .map(this::render)
                .map(this::renderLead)
                .map(a -> {
                    a.setContent(null);
                    a.setFormatted(null);
                    return a;
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/news/{alias}", method = RequestMethod.PUT)
    public void update(@RequestBody News news) {
        repository.save(news);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/news", method = RequestMethod.POST)
    public News create(@RequestBody News news) {
        news.setCreated(new Date());
        return render(repository.insert(news));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/text/news/{alias}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String alias) {
        repository.deleteNewsByAlias(alias);
    }

    public News render(News p) {
        p.setFormatted(textConverter.format("markdown", p.getContent()));
        return p;
    }

    public News renderLead(News p) {
        StringBuilder lead = new StringBuilder();
        Scanner s = new Scanner(p.getContent()).useDelimiter("\\n");
        while (s.hasNext() && lead.length() < 200) {
            lead.append(s.next());
        }
        p.setLeadFormatted(textConverter.format("markdown", lead.toString()));
        if (lead.length() < p.getContent().length()) {
            p.setLongText(true);
        }
        return p;
    }

}
