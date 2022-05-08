package com.aggregation.track;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackInfo {

    private String trackingId;
    private String status;

    public TrackInfo(String trackingId, String status) {
        this.trackingId = trackingId;
        this.status = status;
    }
}
