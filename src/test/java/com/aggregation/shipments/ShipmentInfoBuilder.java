package com.aggregation.shipments;

import java.util.List;

public class ShipmentInfoBuilder {

    private String key;
    private List<String> value;

    private ShipmentInfoBuilder() {
        // Not implemented on purpose
    }

    public static ShipmentInfoBuilder builder() {
        return new ShipmentInfoBuilder();
    }

    public ShipmentInfoBuilder key(String key) {
        this.key = key;
        return this;
    }

    public ShipmentInfoBuilder value(List<String> value) {
        this.value = value;
        return this;
    }

    public ShipmentInfo build() {
        return new ShipmentInfo(key, value);
    }

    public String toString() {
        return "ShipmentInfoBuilder(key=" + this.key + ", value=" + this.value + ")";
    }

}
