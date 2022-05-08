package com.aggregation.aggregation;

import com.aggregation.aggregation.api.AggregationResource;
import com.aggregation.pricing.PricingService;
import com.aggregation.pricing.api.PricingResource;
import com.aggregation.shipments.ShipmentsService;
import com.aggregation.shipments.api.ShipmentsResource;
import com.aggregation.track.TrackService;
import com.aggregation.track.api.TrackResource;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregationUseCaseService {

    private final PricingService pricingService;
    private final TrackService trackService;
    private final ShipmentsService shipmentsService;

    public AggregationResource aggregate(@Nullable List<String> countryCodes, @Nullable List<String> trackNumbers, @Nullable List<String> shipments) {

        List<PricingResource> pricingInfo = Collections.emptyList();

        if (countryCodes != null && !countryCodes.isEmpty()) {
            pricingInfo = pricingService.getPricingInfo(countryCodes);
        }

        List<TrackResource> trackInfo = Collections.emptyList();

        if (trackNumbers != null && !trackNumbers.isEmpty()) {
            trackInfo = trackService.getTrackingInfo(trackNumbers);
        }

        List<ShipmentsResource> shipmentsInfo = Collections.emptyList();

        if (shipmentsInfo != null && !shipmentsInfo.isEmpty()) {
            shipmentsInfo = shipmentsService.getShipmentInfo(shipments);
        }

        return new AggregationResource(pricingInfo, trackInfo, shipmentsInfo);
    }
}
