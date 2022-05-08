package com.aggregation.track.api;

public enum TrackStatusResource {

    NEW ("NEW"),
    IN_TRANSIT ("IN TRANSIT"),
    COLLECTING ("COLLECTING"),
    COLLECTED ("COLLECTED"),
    DELIVERING ("DELIVERING"),
    DELIVERED ("DELIVERED");

    private String value;

    TrackStatusResource(String value) {
        this.value = value;
    }
}
