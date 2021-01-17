package hu.tilos.radio.backend.mix;

import hu.tilos.radio.backend.contribution.ShowReference;

public class Mix {
    private String author;
    private String id;
    private String link;
    private String file;
    private String title;
    private String date;
    private MixCategory category;
    private MixType type;
    private ShowReference show;
    private String content;
    private boolean withContent;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MixCategory getCategory() {
        return category;
    }

    public void setCategory(MixCategory category) {
        this.category = category;
    }

    public MixType getType() {
        return type;
    }

    public void setType(MixType type) {
        this.type = type;
    }

    public ShowReference getShow() {
        return show;
    }

    public void setShow(ShowReference show) {
        this.show = show;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isWithContent() {
        return withContent;
    }

    public void setWithContent(boolean withContent) {
        this.withContent = withContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
