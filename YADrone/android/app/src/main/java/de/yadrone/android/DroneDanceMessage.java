package de.yadrone.android;

/**
 * Created by anatriep on 15/11/2014.
 * It's a FUNKY class!
 */
public class DroneDanceMessage {
    private int midAmplitude;
    private int highAmplitude;
    private int lowAmplitude;
    private long startTimestamp;
    private long endTimestamp;


    public int getHighAmplitude() {
        return highAmplitude;
    }

    public void setHighAmplitude(int highAmplitude) {
        this.highAmplitude = highAmplitude;
    }

    public int getLowAmplitude() {
        return lowAmplitude;
    }

    public void setLowAmplitude(int lowAmplitude) {
        this.lowAmplitude = lowAmplitude;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public int getMidAmplitude() {
        return midAmplitude;
    }

    public void setMidAmplitude(int midAmplitude) {
        this.midAmplitude = midAmplitude;
    }
}
