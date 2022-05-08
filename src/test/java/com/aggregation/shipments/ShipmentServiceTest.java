package com.aggregation.shipments;

import com.aggregation.configuration.QueueTimeoutProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private GetShipmentsService getShipmentsService;
    @Mock
    private QueueTimeoutProperties queueTimeoutProperties;

    @Captor
    private ArgumentCaptor<List<String>> shipmentsCaptor;

    @InjectMocks
    private ShipmentsService shipmentsService;

    @Test
    void getShipmentInfo_calledExactlyFiveTimes() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(1);

        List<String> shipmentIds = new ArrayList<>();
        shipmentIds.add("1234567891");
        shipmentIds.add("109347263");
        shipmentIds.add("23425");
        shipmentIds.add("6364");
        shipmentIds.add("233422424");

        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                shipmentsService.getShipmentInfo(shipmentIds.get(finalI));
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        verify(getShipmentsService).getShipments(shipmentsCaptor.capture());
        List<String> capturedShipmentList = shipmentsCaptor.getValue();

        assertThat(capturedShipmentList.size(), is(5));
    }

    @Test
    void getShipmentInfo_calledTwiceWaitsOnTimeout() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(20);

        List<String> shipmentIds = new ArrayList<>();
        shipmentIds.add("1234567891");
        shipmentIds.add("109347263");

        CountDownLatch countDownLatch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                shipmentsService.getShipmentInfo(shipmentIds.get(finalI));
                countDownLatch.countDown();
            });
            Thread.sleep(10);
        }

        countDownLatch.await();

        verify(getShipmentsService).getShipments(shipmentsCaptor.capture());
        List<String> capturedShipmentList = shipmentsCaptor.getValue();

        assertThat(capturedShipmentList.size(), is(2));
    }
}
