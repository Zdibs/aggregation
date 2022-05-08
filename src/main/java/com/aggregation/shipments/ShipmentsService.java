package com.aggregation.shipments;

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

    private final List<String> queue = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<String>> results = Collections.synchronizedMap(new HashMap<>());

    @Async
    public CompletableFuture<ShipmentInfo> getShipmentInfo(String shipment) {

        try {
            int queueSize;

            synchronized (queue) {
                queue.add(shipment);
                queueSize = queue.size();
            }

            if (queueSize >= 4) {
                getShipments();
            } else {
                synchronized (queue) {
                    queue.wait(5000);
                }

                if(!results.containsKey(shipment)) {
                    getShipments();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO concurrency issue here
        if (!queue.contains(shipment)) {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.remove(shipment)));
        } else {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.get(shipment)));
        }
    }

    private void getShipments() {
        List<String> requestedShipments = new ArrayList<>();

        synchronized (queue) {
            int queueSize = queue.size();

            for (int i = 0; i < 5 && i < queueSize; i++) {
                requestedShipments.add(queue.remove(0));
            }
        }

        Map<String, List<String>> answer = getShipmentsService.getShipments(requestedShipments);

        results.putAll(answer);

        synchronized (queue) {
            queue.notifyAll();
        }
    }
}
