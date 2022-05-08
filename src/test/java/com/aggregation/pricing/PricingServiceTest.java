package com.aggregation.pricing;

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
public class PricingServiceTest {

    @Mock
    private GetPricingService getPricingService;
    @Mock
    private QueueTimeoutProperties queueTimeoutProperties;

    @Captor
    private ArgumentCaptor<List<String>> pricingCaptor;

    @InjectMocks
    private PricingService pricingService;

    @Test
    void getShipmentInfo_calledExactlyFiveTimes() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(1);

        List<String> countryCodes = new ArrayList<>();
        countryCodes.add("CN");
        countryCodes.add("NL");
        countryCodes.add("BE");
        countryCodes.add("DE");
        countryCodes.add("FR");

        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                pricingService.getPricingInfo(countryCodes.get(finalI));
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        verify(getPricingService).getPricingInfo(pricingCaptor.capture());
        List<String> capturedPricingList = pricingCaptor.getValue();

        assertThat(capturedPricingList.size(), is(5));
    }

    @Test
    void getShipmentInfo_calledTwiceWaitsOnTimeout() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(20);

        List<String> countryCodes = new ArrayList<>();
        countryCodes.add("CN");
        countryCodes.add("NL");

        CountDownLatch countDownLatch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                pricingService.getPricingInfo(countryCodes.get(finalI));
                countDownLatch.countDown();
            });
            Thread.sleep(10);
        }

        countDownLatch.await();

        verify(getPricingService).getPricingInfo(pricingCaptor.capture());
        List<String> capturedPricingList = pricingCaptor.getValue();

        assertThat(capturedPricingList.size(), is(2));
    }
}
