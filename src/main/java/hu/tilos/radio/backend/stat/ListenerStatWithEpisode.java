package hu.tilos.radio.backend.stat;

import hu.tilos.radio.backend.episode.EpisodeData;

public class ListenerStatWithEpisode {

    private EpisodeData episode;

    private int max;

    private int mean;

    private int min;

    public ListenerStatWithEpisode() {
    }

    public ListenerStatWithEpisode(EpisodeData episode, ListenerStat listenerStat) {
        this.episode = episode;
        this.max = listenerStat.getMax();
        this.mean = listenerStat.getMean();
        this.min = listenerStat.getMin();
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public EpisodeData getEpisode() {
        return episode;
    }

    public void setEpisode(EpisodeData episode) {
        this.episode = episode;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMean() {
        return mean;
    }

    public void setMean(int mean) {
        this.mean = mean;
    }
}
