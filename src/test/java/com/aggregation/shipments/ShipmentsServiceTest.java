package com.aggregation.shipments;

import com.aggregation.configuration.ApiProperties;
import com.aggregation.okhttp.OkHttpCallManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentsServiceTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ApiProperties apiProperties;
    @Mock
    private OkHttpCallManager okHttpCallManager;

    @Captor
    private ArgumentCaptor<String> requestUrlCaptor;

    @InjectMocks
    private ShipmentsService shipmentsService;

    @Test
    void getShipmentInfo_success() throws IOException, ExecutionException, InterruptedException {
        // given
        String shipments1 = "2136544";
        String shipments2 = "8975543";

        String objectMapperString = "testObjectMapperString";
        when(okHttpCallManager.call(requestUrlCaptor.capture())).thenReturn(objectMapperString);

        String hostname = "testHostname";
        when(apiProperties.getHostname()).thenReturn(hostname);
        String port = "testPort";
        when(apiProperties.getPort()).thenReturn(port);
        String shipmentsEndpoint = "testShipments";
        when(apiProperties.getShipmentsEndpoint()).thenReturn(shipmentsEndpoint);

        HashMap<String, List<String>> objectMapperAnswer = new HashMap<>();
        objectMapperAnswer.put(shipments1, Arrays.asList("box", "box"));
        objectMapperAnswer.put(shipments2, Arrays.asList("envelope", "box"));
        when(objectMapper.readValue(eq(objectMapperString), any(TypeReference.class))).thenReturn(objectMapperAnswer);

        // when
        List<String> shipments = Arrays.asList(shipments1, shipments2);
        Map<String, List<String>> result = shipmentsService.getShipmentInfo(shipments).get();

        // then
        String requestUrl = requestUrlCaptor.getValue();
        assertThat(requestUrl, is("http://" + hostname + ":" + port +
                "/" + shipmentsEndpoint + "?q=" + String.join(",", shipments)));
        assertThat(result, is(objectMapperAnswer));
    }

    @Test
    void getShipmentInfo_shouldHandleEmptyShipmentsList() throws ExecutionException, InterruptedException {
        Map<String, List<String>> result = shipmentsService.getShipmentInfo(Collections.emptyList()).get();

        assertThat(result, is(nullValue()));
        verifyNoInteractions(okHttpCallManager);
    }

    @Test
    void getShipmentInfo_shouldHandleResultThatContainsMessageKeyword() throws JsonProcessingException, ExecutionException, InterruptedException {
        // given
        String shipments1 = "2136544";
        String shipments2 = "8975543";

        String objectMapperString = "testObjectMapperString";
        when(okHttpCallManager.call(requestUrlCaptor.capture())).thenReturn(objectMapperString);

        String hostname = "testHostname";
        when(apiProperties.getHostname()).thenReturn(hostname);
        String port = "testPort";
        when(apiProperties.getPort()).thenReturn(port);
        String shipmentsEndpoint = "testShipments";
        when(apiProperties.getShipmentsEndpoint()).thenReturn(shipmentsEndpoint);

        HashMap<String, List<String>> objectMapperAnswer = new HashMap<>();
        objectMapperAnswer.put("message", Arrays.asList("box", "box"));
        when(objectMapper.readValue(eq(objectMapperString), any(TypeReference.class))).thenReturn(objectMapperAnswer);

        // when
        List<String> shipments = Arrays.asList(shipments1, shipments2);
        Map<String, List<String>> result = shipmentsService.getShipmentInfo(shipments).get();

        // then
        String requestUrl = requestUrlCaptor.getValue();
        assertThat(requestUrl, is("http://" + hostname + ":" + port +
                "/" + shipmentsEndpoint + "?q=" + String.join(",", shipments)));
        assertThat(result, is(nullValue()));
    }

}
