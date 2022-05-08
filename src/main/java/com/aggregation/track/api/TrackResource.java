package com.aggregation.track.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackResource {

    private String trackingId;

    private TrackStatusResource trackStatus;
}
