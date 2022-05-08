package com.aggregation.aggregation.api;

import com.aggregation.pricing.api.PricingResource;
import com.aggregation.shipments.api.ShipmentsResource;
import com.aggregation.track.api.TrackResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AggregationResource {

    private List<PricingResource> pricingResources;

    private List<TrackResource> trackResources;

    private List<ShipmentsResource> shipmentsResources;
}
