package com.aggregation.shipments;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShipmentInfo {

    private String key;
    private List<String> value;

    public ShipmentInfo(String key, List<String> value) {
        this.key = key;
        this.value = value;
    }
}
