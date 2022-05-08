package com.aggregation.aggregation;

import com.aggregation.aggregation.api.AggregationResource;
import com.aggregation.pricing.PricingService;
import com.aggregation.shipments.ShipmentInfo;
import com.aggregation.shipments.ShipmentInfoBuilder;
import com.aggregation.shipments.ShipmentsService;
import com.aggregation.track.TrackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
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
        String pricing1 = "CN";
        String pricing2 = "NL";
        List<String> countryCodes = Arrays.asList(pricing1, pricing2);

        HashMap<String, String> obtainedPricing = new HashMap<>();
        obtainedPricing.put(pricing1, "22.956661391130684");
        obtainedPricing.put(pricing2, "73.98055140423651");

        when(pricingService.getPricingInfo(countryCodes)).thenReturn(CompletableFuture.completedFuture(obtainedPricing));

        String track1 = "109347263";
        String track2 = "1234567891";
        List<String> trackNumbers = Arrays.asList(track1, track2);

        HashMap<String, String> obtainedTrack = new HashMap<>();
        obtainedTrack.put(track1, "DELIVERING");
        obtainedTrack.put(track2, "IN TRANSIT");

        when(trackService.getTrackingInfo(trackNumbers)).thenReturn(CompletableFuture.completedFuture(obtainedTrack));

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

        assertThat(aggregationResource.getPricing(), is(obtainedPricing));
        assertThat(aggregationResource.getShipments().size(), is(2));
        assertThat(aggregationResource.getShipments().get(shipments1), is(shipmentInfo1.getValue()));
        assertThat(aggregationResource.getShipments().get(shipments2), is(shipmentInfo2.getValue()));
        assertThat(aggregationResource.getTrack(), is(obtainedTrack));
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
