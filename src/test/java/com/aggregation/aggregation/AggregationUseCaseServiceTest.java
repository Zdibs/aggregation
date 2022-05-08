package com.aggregation.aggregation;

import com.aggregation.aggregation.api.AggregationResource;
import com.aggregation.pricing.PricingInfo;
import com.aggregation.pricing.PricingInfoBuilder;
import com.aggregation.pricing.PricingService;
import com.aggregation.shipments.ShipmentInfo;
import com.aggregation.shipments.ShipmentInfoBuilder;
import com.aggregation.shipments.ShipmentsService;
import com.aggregation.track.TrackInfo;
import com.aggregation.track.TrackInfoBuilder;
import com.aggregation.track.TrackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AggregationUseCaseServiceTest {

    @Mock
    private PricingService pricingService;
    @Mock
    private TrackService trackService;
    @Mock
    private ShipmentsService shipmentsService;

    @InjectMocks
    private AggregationUseCaseService aggregationUseCaseService;

    @Test
    void aggregate_allListsAreNonNull() {
        String countryCode1 = "CN";
        String countryCode2 = "NL";
        List<String> countryCodes = Arrays.asList(countryCode1, countryCode2);

        PricingInfo pricingInfo1 = PricingInfoBuilder.builder()
                .countryCode(countryCode1)
                .price("22.956661391130684")
                .build();
        PricingInfo pricingInfo2 = PricingInfoBuilder.builder()
                .countryCode(countryCode2)
                .price("73.98055140423651")
                .build();

        when(pricingService.getPricingInfo(countryCode1)).thenReturn(CompletableFuture.completedFuture(pricingInfo1));
        when(pricingService.getPricingInfo(countryCode2)).thenReturn(CompletableFuture.completedFuture(pricingInfo2));

        String trackingId1 = "109347263";
        String trackingId2 = "1234567891";
        List<String> trackNumbers = Arrays.asList(trackingId1, trackingId2);

        TrackInfo trackInfo1 = TrackInfoBuilder.builder()
                .trackingId(trackingId1)
                .status("DELIVERING")
                .build();
        TrackInfo trackInfo2 = TrackInfoBuilder.builder()
                .trackingId(trackingId2)
                .status("IN TRANSIT")
                .build();

        when(trackService.getTrackInfo(trackingId1)).thenReturn(CompletableFuture.completedFuture(trackInfo1));
        when(trackService.getTrackInfo(trackingId2)).thenReturn(CompletableFuture.completedFuture(trackInfo2));

        String shipments1 = "2136544";
        String shipments2 = "8975543";
        List<String> shipments = Arrays.asList(shipments1, shipments2);

        ShipmentInfo shipmentInfo1 = ShipmentInfoBuilder.builder()
                .key(shipments1)
                .value(Arrays.asList("box", "box"))
                .build();
        ShipmentInfo shipmentInfo2 = ShipmentInfoBuilder.builder()
                .key(shipments2)
                .value(Arrays.asList("envelope", "box"))
                .build();
        when(shipmentsService.getShipmentInfo(shipments1)).thenReturn(CompletableFuture.completedFuture(shipmentInfo1));
        when(shipmentsService.getShipmentInfo(shipments2)).thenReturn(CompletableFuture.completedFuture(shipmentInfo2));

        AggregationResource aggregationResource = aggregationUseCaseService.aggregate(countryCodes, trackNumbers, shipments);

        assertThat(aggregationResource.getPricing().size(), is(2));
        assertThat(aggregationResource.getPricing().get(countryCode1), is(pricingInfo1.getPrice()));
        assertThat(aggregationResource.getPricing().get(countryCode2), is(pricingInfo2.getPrice()));
        assertThat(aggregationResource.getShipments().size(), is(2));
        assertThat(aggregationResource.getShipments().get(shipments1), is(shipmentInfo1.getValue()));
        assertThat(aggregationResource.getShipments().get(shipments2), is(shipmentInfo2.getValue()));
        assertThat(aggregationResource.getTrack().size(), is(2));
        assertThat(aggregationResource.getTrack().get(trackingId1), is(trackInfo1.getStatus()));
        assertThat(aggregationResource.getTrack().get(trackingId2), is(trackInfo2.getStatus()));
    }

    @Test
    void aggregate_allListsAreNull() {
        List<String> countryCodes = null;
        List<String> trackNumbers = null;
        List<String> shipments = null;

        AggregationResource aggregationResource = aggregationUseCaseService.aggregate(countryCodes, trackNumbers, shipments);

        assertThat(aggregationResource.getPricing(), is(nullValue()));
        assertThat(aggregationResource.getPricing(), is(nullValue()));
        assertThat(aggregationResource.getPricing(), is(nullValue()));

        verifyNoInteractions(pricingService);
        verifyNoInteractions(trackService);
        verifyNoInteractions(shipmentsService);
    }
}
