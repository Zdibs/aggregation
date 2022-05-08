package com.aggregation.shipments;

import com.aggregation.shipments.api.ShipmentsResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ShipmentsService {

    public List<ShipmentsResource> getShipmentInfo(List<String> shipments) {
        return Collections.emptyList();
    }
}
