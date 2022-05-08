package com.aggregation.aggregation;

import com.aggregation.aggregation.api.AggregationResource;
import com.aggregation.pricing.PricingService;
import com.aggregation.shipments.ShipmentsService;
import com.aggregation.track.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AggregationUseCaseService {

    private final PricingService pricingService;
    private final TrackService trackService;
    private final ShipmentsService shipmentsService;

    public AggregationResource aggregate(@Nullable List<String> countryCodes, @Nullable List<String> trackNumbers, @Nullable List<String> shipments) {

        CompletableFuture<Map<String, String>> pricingInfo = null;

        if (countryCodes != null && !countryCodes.isEmpty()) {
            pricingInfo = pricingService.getPricingInfo(countryCodes);
        }

        CompletableFuture<Map<String, String>> trackInfo = null;

        if (trackNumbers != null && !trackNumbers.isEmpty()) {
            trackInfo = trackService.getTrackingInfo(trackNumbers);
        }

        CompletableFuture<Map<String, List<String>>> shipmentsInfo = null;

        if (shipments != null && !shipments.isEmpty()) {
            shipmentsInfo = shipmentsService.getShipmentInfo(shipments);
        }

        return new AggregationResource(pricingInfo != null ? pricingInfo.join() : null,
                trackInfo != null ? trackInfo.join() : null,
                shipmentsInfo != null ? shipmentsInfo.join(): null);
    }
}
