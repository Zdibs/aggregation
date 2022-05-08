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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@Scope("singleton")
@RequiredArgsConstructor
public class ShipmentsService {

    private final GetShipmentsService getShipmentsService;

    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
        @Override
        public void run() {
            List<String> requestedShipments = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                requestedShipments.add(requests.remove(0));
            }

            Map<String, List<String>> answer = getShipmentsService.getShipments(requestedShipments);

            results.putAll(answer);
        }
    });

    private final List<String> requests = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<String>> results = Collections.synchronizedMap(new HashMap<>());

    @Async
    public CompletableFuture<ShipmentInfo> getShipmentInfo(String shipment) {

        requests.add(shipment);

        try {
            cyclicBarrier.await(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            return getSingleShipment(shipment);
        } catch (TimeoutException e) {
            cyclicBarrier.reset();
            return getSingleShipment(shipment);
        }

        if (!requests.contains(shipment)) {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.remove(shipment)));
        } else {
            return CompletableFuture.completedFuture(new ShipmentInfo(shipment, results.get(shipment)));
        }
    }

    private CompletableFuture<ShipmentInfo> getSingleShipment(String shipment) {
        Map<String, List<String>> answer = getShipmentsService.getShipments(Collections.singletonList(shipment));
        return CompletableFuture.completedFuture(new ShipmentInfo(shipment, answer.get(shipment)));
    }

}
