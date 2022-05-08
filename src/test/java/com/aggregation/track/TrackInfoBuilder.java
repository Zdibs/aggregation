package com.aggregation.track;

public class TrackInfoBuilder {

    private String trackingId;
    private String status;

    private TrackInfoBuilder() {
        // Not implemented on purpose
    }

    public static TrackInfoBuilder builder() {
        return new TrackInfoBuilder();
    }

    public TrackInfoBuilder trackingId(String trackingId) {
        this.trackingId = trackingId;
        return this;
    }

    public TrackInfoBuilder status(String status) {
        this.status = status;
        return this;
    }

    public TrackInfo build() {
        return new TrackInfo(trackingId, status);
    }

    public String toString() {
        return "TrackInfo.TrackInfoBuilder(trackingId=" + this.trackingId + ", status=" + this.status + ")";
    }
}
