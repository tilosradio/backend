package hu.tilos.radio.backend.episode;

import java.util.Date;

public class EpisodeBase {

    private Date plannedFrom;

    private Date plannedTo;

    private Date realFrom;

    private Date realTo;

    public Date getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(Date plannedFrom) {
        this.plannedFrom = plannedFrom;
    }

    public Date getPlannedTo() {
        return plannedTo;
    }

    public void setPlannedTo(Date plannedTo) {
        this.plannedTo = plannedTo;
    }

    public Date getRealFrom() {
        return realFrom;
    }

    public void setRealFrom(Date realFrom) {
        this.realFrom = realFrom;
    }

    public Date getRealTo() {
        return realTo;
    }

    public void setRealTo(Date realTo) {
        this.realTo = realTo;
    }


    public boolean isInThePast() {
        return getPlannedTo().getTime() < new Date().getTime() + 60 * 6 * 1000;
    }

}
