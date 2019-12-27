package hu.tilos.radio.backend.show;


import hu.tilos.radio.backend.scheduling.SchedulingToSave;
import hu.tilos.radio.backend.data.input.UrlToSave;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


public class ShowToSave {

    @NotNull
    public String alias;

    @NotNull
    private String name;

    private String definition;

    private String description;

    private String introduction;

    private ShowType type;

    private ShowStatus status;

    private List<UrlToSave> urls = new ArrayList<>();

    private List<SchedulingToSave> schedulings = new ArrayList<>();

    public List<SchedulingToSave> getSchedulings() {
        return schedulings;
    }

    public void setSchedulings(List<SchedulingToSave> schedulings) {
        this.schedulings = schedulings;
    }

    public List<UrlToSave> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlToSave> urls) {
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public ShowType getType() {
        return type;
    }

    public void setType(ShowType type) {
        this.type = type;
    }

    public ShowStatus getStatus() {
        return status;
    }

    public void setStatus(ShowStatus status) {
        this.status = status;
    }

}
