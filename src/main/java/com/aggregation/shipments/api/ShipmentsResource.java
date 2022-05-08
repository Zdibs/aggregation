package com.aggregation.shipments.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShipmentsResource {

    private String shipmentId;

    private List<String> productTypes;
}
