package hu.tilos.radio.backend.stat;

public class ListenerStat {

    private int max;

    private int mean;

    private int min;

    public ListenerStat() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
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
