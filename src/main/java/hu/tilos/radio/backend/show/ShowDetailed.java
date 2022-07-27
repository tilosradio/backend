package hu.tilos.radio.backend.show;

import hu.tilos.radio.backend.contribution.ShowContribution;
import hu.tilos.radio.backend.scheduling.SchedulingSimple;
import hu.tilos.radio.backend.data.types.UrlData;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailed {

    private String id;

    private String name;

    private String title;

    private String alias;

    private String definition;

    private String description;

    private ShowType type;

    private ShowStatus status;

    private String statusTxt;

    private Long firstShowDate;

    private Long lastShowDate;

    private ShowStats stats = new ShowStats();

    public ShowStats getStats() {
        return stats;
    }

    public void setStats(ShowStats stats) {
        this.stats = stats;
    }

    private List<String> mixes = new ArrayList<>();

    private List<ShowContribution> contributors = new ArrayList<>();

    private List<SchedulingSimple> schedulings = new ArrayList<>();

    public List<UrlData> urls = new ArrayList<>();

    public List<ShowContribution> getContributors() {
        return contributors;
    }

    public List<SchedulingSimple> getSchedulings() {
        return schedulings;
    }

    public List<UrlData> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlData> urls) {
        this.urls = urls;
    }

    public void setSchedulings(List<SchedulingSimple> schedulings) {
        this.schedulings = schedulings;
    }

    public void setContributors(List<ShowContribution> contributors) {
        this.contributors = contributors;
    }

    public String getStatusTxt() {
        return statusTxt;
    }

    public void setStatusTxt(String statusTxt) {
        this.statusTxt = statusTxt;
    }

    public List<String> getMixes() {
        return mixes;
    }

    public void setMixes(List<String> mixes) {
        this.mixes = mixes;
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

    public String getAnyDescription() {
        return getAnyDescription("");
    }

    public String getAnyDescription(String fallback) {
        if (description != null && !description.isEmpty()) {
            return description;
        }
        if (definition != null && !definition.isEmpty()) {
            return definition;
        }
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return fallback;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Long getFirstShowDate() {
        return firstShowDate;
    }

    public void setFirstShowDate(Long firstShowDate) {
        this.firstShowDate = firstShowDate;
    }

    public Long getLastShowDate() {
        return lastShowDate;
    }

    public void setLastShowDate(Long lastShowDate) {
        this.lastShowDate = lastShowDate;
    }
}
