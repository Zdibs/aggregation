package com.aggregation.aggregation;

import com.aggregation.aggregation.api.AggregationResource;
import com.aggregation.pricing.PricingInfo;
import com.aggregation.pricing.PricingService;
import com.aggregation.shipments.ShipmentInfo;
import com.aggregation.shipments.ShipmentsService;
import com.aggregation.track.TrackInfo;
import com.aggregation.track.TrackService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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

        // Send out all asynchronous calls
        List<CompletableFuture<PricingInfo>> pricingInfoCompletableFutures = getFuturesForPricing(countryCodes);

        List<CompletableFuture<TrackInfo>> trackInfoCompletableFutures = getFuturesForTracking(trackNumbers);

        List<CompletableFuture<ShipmentInfo>> shipmentsInfoCompletableFutures = getFuturesForShipments(shipments);

        // join all asynchronous calls
        Map<String, List<String>> obtainedShipmentsInfo = new HashMap<>();
        for (CompletableFuture<ShipmentInfo> shipmentCompletableFuture : shipmentsInfoCompletableFutures) {
            ShipmentInfo shipmentInfo = shipmentCompletableFuture.join();
            obtainedShipmentsInfo.put(shipmentInfo.getKey(), shipmentInfo.getValue());
        }

        Map<String, String> obtainedPricingInfo = new HashMap<>();
        for (CompletableFuture<PricingInfo> pricingCompletableFuture : pricingInfoCompletableFutures) {
            PricingInfo pricingInfo = pricingCompletableFuture.join();
            obtainedPricingInfo.put(pricingInfo.getCountryCode(), pricingInfo.getPrice());
        }

        Map<String, String> obtainedTrackInfo = new HashMap<>();
        for (CompletableFuture<TrackInfo> trackingCompletableFuture : trackInfoCompletableFutures) {
            TrackInfo trackInfo = trackingCompletableFuture.join();
            obtainedTrackInfo.put(trackInfo.getTrackingId(), trackInfo.getStatus());
        }

        if (obtainedShipmentsInfo.isEmpty()) {
            obtainedShipmentsInfo = null;
        }

        if (obtainedPricingInfo.isEmpty()) {
            obtainedPricingInfo = null;
        }

        if (obtainedTrackInfo.isEmpty()) {
            obtainedTrackInfo = null;
        }

        return new AggregationResource(obtainedPricingInfo,
                obtainedTrackInfo,
                obtainedShipmentsInfo);
    }

    @NotNull
    private List<CompletableFuture<ShipmentInfo>> getFuturesForShipments(List<String> shipments) {
        List<CompletableFuture<ShipmentInfo>> shipmentsInfoCompletableFutures = new ArrayList<>();

        if (shipments != null) {
            for (String shipment : shipments) {
                shipmentsInfoCompletableFutures.add(shipmentsService.getShipmentInfo(shipment));
            }
        }
        return shipmentsInfoCompletableFutures;
    }

    @NotNull
    private List<CompletableFuture<TrackInfo>> getFuturesForTracking(List<String> trackNumbers) {
        List<CompletableFuture<TrackInfo>> trackInfoCompletableFutures = new ArrayList<>();

        if (trackNumbers != null) {
            for (String trackId : trackNumbers) {
                trackInfoCompletableFutures.add(trackService.getTrackInfo(trackId));
            }
        }
        return trackInfoCompletableFutures;
    }

    @NotNull
    private List<CompletableFuture<PricingInfo>> getFuturesForPricing(List<String> countryCodes) {
        List<CompletableFuture<PricingInfo>> pricingInfoCompletableFutures = new ArrayList<>();

        if (countryCodes != null) {
            for (String countryCode : countryCodes) {
                pricingInfoCompletableFutures.add(pricingService.getPricingInfo(countryCode));
            }
        }
        return pricingInfoCompletableFutures;
    }
}
