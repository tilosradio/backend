package hu.tilos.radio.backend.recommendation;

import hu.tilos.radio.backend.data.Author;

import java.util.Date;

public class RecommendationSimple {
    private String id;
    private RecommendationType type;
    private String title;
    private String writer;
    private String link;
    private String image;
    private Date date;
    private Author author;
    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecommendationType getType() {
        return type;
    }

    public void setType(RecommendationType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = RecommendationType.valueOf(type);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date createdAt) {
        this.created = createdAt;
    }
}
