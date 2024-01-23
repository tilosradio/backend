package hu.tilos.radio.backend.recommendation;

import hu.tilos.radio.backend.data.Author;
import hu.tilos.radio.backend.episode.EpisodeSimple;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class RecommendationToSave {

    @NotNull
    private RecommendationType type;
    @NotNull
    private String title;
    private String description;
    private String link;
    private String image;
    private EpisodeSimple episode;
    private Date date;
    private String writer;

    public RecommendationType getType() {
        return type;
    }

    private Author author;

    public void setType(RecommendationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EpisodeSimple getEpisode() {
        return episode;
    }

    public void setEpisode(EpisodeSimple episode) {
        this.episode = episode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}