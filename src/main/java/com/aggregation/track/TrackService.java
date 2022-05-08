package com.aggregation.track;

import com.aggregation.configuration.QueueTimeoutProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Scope("singleton")
@RequiredArgsConstructor
public class TrackService {

    private final GetTrackService getTrackService;
    private final QueueTimeoutProperties queueTimeoutProperties;

    private final List<String> queue = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, String> results = Collections.synchronizedMap(new HashMap<>());

    @Async
    public CompletableFuture<TrackInfo> getTrackInfo(String trackingId) {
        try {
            List<String> requestedPricings = new ArrayList<>();
            List<String> requestedPricingsDueToTimeout = new ArrayList<>();

            addToQueueAndCheckIfQueueHasEnoughEntriesToRequestData(trackingId, requestedPricings);

            if (!requestedPricings.isEmpty()) {
                getShipments(requestedPricings);
            } else {
                waitForEnoughEntriesAndRequestDataAfterTimeout(trackingId, requestedPricingsDueToTimeout);
            }

            if (!requestedPricingsDueToTimeout.isEmpty()) {
                getShipments(requestedPricingsDueToTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Here a concurrency issue remains for the case where the same results have been requested multiple times
        if (!queue.contains(trackingId)) {
            return CompletableFuture.completedFuture(new TrackInfo(trackingId, results.remove(trackingId)));
        } else {
            return CompletableFuture.completedFuture(new TrackInfo(trackingId, results.get(trackingId)));
        }
    }

    private void waitForEnoughEntriesAndRequestDataAfterTimeout(String shipment, List<String> requestedShipmentsDueToTimeout) throws InterruptedException {
        synchronized (queue) {
            queue.wait(queueTimeoutProperties.getTimeoutInMilliseconds());

            if(!results.containsKey(shipment)) {
                int queueSize = queue.size();
                for (int i = 0; i < 5 && i < queueSize; i++) {
                    requestedShipmentsDueToTimeout.add(queue.remove(0));
                }
            }
        }
    }

    private void addToQueueAndCheckIfQueueHasEnoughEntriesToRequestData(String shipment, List<String> requestedShipments) {
        synchronized (queue) {
            queue.add(shipment);

            if (queue.size() == 5) {
                for (int i = 0; i < 5; i++) {
                    requestedShipments.add(queue.remove(0));
                }
            }
        }
    }

    private void getShipments(List<String> trackRequests) {
        Map<String, String> answer = getTrackService.getTrackingInfo(trackRequests);

        if (answer != null) {
            results.putAll(answer);
        }

        synchronized (queue) {
            queue.notifyAll();
        }
    }
}
