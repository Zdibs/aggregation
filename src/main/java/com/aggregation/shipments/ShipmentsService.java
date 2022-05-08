package com.aggregation.shipments;

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
public class ShipmentsService {

    private final GetShipmentsService getShipmentsService;
    private final QueueTimeoutProperties queueTimeoutProperties;

    private final List<String> queue = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<String>> results = Collections.synchronizedMap(new HashMap<>());

    @Async
    public CompletableFuture<ShipmentInfo> getShipmentInfo(String shipment) {
        try {
            List<String> requestedShipments = new ArrayList<>();
            List<String> requestedShipmentsDueToTimeout = new ArrayList<>();

            addToQueueAndCheckIfQueueHasEnoughEntriesToRequestData(shipment, requestedShipments);

            if (!requestedShipments.isEmpty()) {
                getShipments(requestedShipments);
            } else {
                waitForEnoughEntriesAndRequestDataAfterTimeout(shipment, requestedShipmentsDueToTimeout);
            }

            if (!requestedShipmentsDueToTimeout.isEmpty()) {
                getShipments(requestedShipmentsDueToTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Here a concurrency issue remains for the case where the same results have been requested multiple times
        if (!queue.contains(shipment)) {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.remove(shipment)));
        } else {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.get(shipment)));
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

    private void getShipments(List<String> requestedShipments) {
        Map<String, List<String>> answer = getShipmentsService.getShipments(requestedShipments);

        results.putAll(answer);

        synchronized (queue) {
            queue.notifyAll();
        }
    }
}
