package com.aggregation.track;

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
public class TrackServiceTest {

    @Mock
    private GetTrackService getTrackService;
    @Mock
    private QueueTimeoutProperties queueTimeoutProperties;

    @Captor
    private ArgumentCaptor<List<String>> trackCaptor;

    @InjectMocks
    private TrackService trackService;

    @Test
    void getTrackingInfo_calledExactlyFiveTimes() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(100);

        List<String> trackingIds = new ArrayList<>();
        trackingIds.add("1234567891");
        trackingIds.add("109347263");
        trackingIds.add("23425");
        trackingIds.add("6364");
        trackingIds.add("233422424");

        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                trackService.getTrackInfo(trackingIds.get(finalI));
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        verify(getTrackService).getTrackingInfo(trackCaptor.capture());
        List<String> capturedTrackList = trackCaptor.getValue();

        assertThat(capturedTrackList.size(), is(5));
    }

    @Test
    void getTrackingInfo_calledTwiceWaitsOnTimeout() throws InterruptedException {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.initialize();

        when(queueTimeoutProperties.getTimeoutInMilliseconds()).thenReturn(100);

        List<String> trackingIds = new ArrayList<>();
        trackingIds.add("1234567891");
        trackingIds.add("109347263");

        CountDownLatch countDownLatch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                trackService.getTrackInfo(trackingIds.get(finalI));
                countDownLatch.countDown();
            });
            Thread.sleep(20);
        }

        countDownLatch.await();

        verify(getTrackService).getTrackingInfo(trackCaptor.capture());
        List<String> capturedTrackList = trackCaptor.getValue();

        assertThat(capturedTrackList.size(), is(2));
    }
}
