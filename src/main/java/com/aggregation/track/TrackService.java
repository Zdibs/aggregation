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
            List<String> requestedTrackingDueToTimeout = new ArrayList<>();

            List<String> requestedTracking = addToQueueAndCheckIfQueueHasEnoughEntriesToRequestData(trackingId);

            if (!requestedTracking.isEmpty()) {
                getTrackingInfo(requestedTracking);
            } else {
                requestedTrackingDueToTimeout.addAll(waitForEnoughEntriesAndRequestDataAfterTimeout(trackingId));
            }

            if (!requestedTrackingDueToTimeout.isEmpty()) {
                getTrackingInfo(requestedTrackingDueToTimeout);
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

    private List<String> waitForEnoughEntriesAndRequestDataAfterTimeout(String trackingId) throws InterruptedException {
        List<String> requestedTrackingIdsDueToTimeout = new ArrayList<>();

        synchronized (queue) {
            queue.wait(queueTimeoutProperties.getTimeoutInMilliseconds());

            if(!results.containsKey(trackingId)) {
                int queueSize = queue.size();
                for (int i = 0; i < 5 && i < queueSize; i++) {
                    requestedTrackingIdsDueToTimeout.add(queue.remove(0));
                }
            }
        }

        return requestedTrackingIdsDueToTimeout;
    }

    private List<String> addToQueueAndCheckIfQueueHasEnoughEntriesToRequestData(String trackingId) {
        List<String> requestedTrackingIds = new ArrayList<>();

        synchronized (queue) {
            queue.add(trackingId);

            if (queue.size() == 5) {
                for (int i = 0; i < 5; i++) {
                    requestedTrackingIds.add(queue.remove(0));
                }
            }
        }

        return requestedTrackingIds;
    }

    private void getTrackingInfo(List<String> trackRequests) {
        Map<String, String> answer = getTrackService.getTrackingInfo(trackRequests);

        if (answer != null) {
            results.putAll(answer);
        }

        synchronized (queue) {
            queue.notifyAll();
        }
    }
}
