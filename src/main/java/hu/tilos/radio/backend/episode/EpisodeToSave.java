package hu.tilos.radio.backend.episode;

import hu.tilos.radio.backend.show.ShowSimple;
import hu.tilos.radio.backend.tag.TagData;
import hu.tilos.radio.backend.text.TextData;

import java.util.List;


public class EpisodeToSave extends EpisodeBase {

    private List<TagData> tags;

    private ShowSimple show;

    private TextData text;

    public List<TagData> getTags() {
        return tags;
    }

    public void setTags(List<TagData> tags) {
        this.tags = tags;
    }

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
    }

    public TextData getText() {
        return text;
    }

    public void setText(TextData text) {
        this.text = text;
    }
}
