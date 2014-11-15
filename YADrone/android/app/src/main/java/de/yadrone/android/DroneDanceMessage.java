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

    private DroneDanceMessage(Builder builder) {
        setMidAmplitude(builder.midAmplitude);
        setHighAmplitude(builder.highAmplitude);
        setLowAmplitude(builder.lowAmplitude);
        setStartTimestamp(builder.startTimestamp);
        setEndTimestamp(builder.endTimestamp);
    }


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

    public static final class Builder {
        private int midAmplitude;
        private int highAmplitude;
        private int lowAmplitude;
        private long startTimestamp;
        private long endTimestamp;

        public Builder() {
        }

        public Builder midAmplitude(int midAmplitude) {
            this.midAmplitude = midAmplitude;
            return this;
        }

        public Builder highAmplitude(int highAmplitude) {
            this.highAmplitude = highAmplitude;
            return this;
        }

        public Builder lowAmplitude(int lowAmplitude) {
            this.lowAmplitude = lowAmplitude;
            return this;
        }

        public Builder startTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder endTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public DroneDanceMessage build() {
            return new DroneDanceMessage(this);
        }
    }
}
