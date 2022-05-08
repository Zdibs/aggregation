package com.aggregation.pricing;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ApiProperties apiProperties;
    @Mock
    private OkHttpCallManager okHttpCallManager;

    @Captor
    private ArgumentCaptor<String> requestUrlCaptor;

    @InjectMocks
    private PricingService pricingService;

    @Test
    void getPricingInfo_success() throws IOException {
        // given
        String pricing1 = "CN";
        String pricing2 = "NL";

        String objectMapperString = "testObjectMapperString";
        when(okHttpCallManager.call(requestUrlCaptor.capture())).thenReturn(objectMapperString);

        String hostname = "testHostname";
        when(apiProperties.getHostname()).thenReturn(hostname);
        String port = "testPort";
        when(apiProperties.getPort()).thenReturn(port);
        String pricingEndpoint = "testPricing";
        when(apiProperties.getPricingEndpoint()).thenReturn(pricingEndpoint);

        HashMap<String, String> objectMapperAnswer = new HashMap<>();
        objectMapperAnswer.put(pricing1, "22.956661391130684");
        objectMapperAnswer.put(pricing2, "73.98055140423651");
        when(objectMapper.readValue(eq(objectMapperString), any(TypeReference.class))).thenReturn(objectMapperAnswer);

        // when
        List<String> pricing = Arrays.asList(pricing1, pricing2);
        Map<String, String> result = pricingService.getPricingInfo(pricing);

        // then
        String requestUrl = requestUrlCaptor.getValue();
        assertThat(requestUrl, is("http://" + hostname + ":" + port +
                "/" + pricingEndpoint + "?q=" + String.join(",", pricing)));
        assertThat(result, is(objectMapperAnswer));
    }

    @Test
    void getPricingInfo_shouldHandleEmptyShipmentsList() {
        Map<String, String> result = pricingService.getPricingInfo(Collections.emptyList());

        assertThat(result, is(nullValue()));
        verifyNoInteractions(okHttpCallManager);
    }

    @Test
    void getPricingInfo_shouldHandleResultThatContainsMessageKeyword() throws JsonProcessingException {
        // given
        String pricing1 = "CN";
        String pricing2 = "NL";

        String objectMapperString = "testObjectMapperString";
        when(okHttpCallManager.call(requestUrlCaptor.capture())).thenReturn(objectMapperString);

        String hostname = "testHostname";
        when(apiProperties.getHostname()).thenReturn(hostname);
        String port = "testPort";
        when(apiProperties.getPort()).thenReturn(port);
        String pricingEndpoint = "testPricing";
        when(apiProperties.getPricingEndpoint()).thenReturn(pricingEndpoint);

        HashMap<String, String> objectMapperAnswer = new HashMap<>();
        objectMapperAnswer.put("message", "Service not available");
        when(objectMapper.readValue(eq(objectMapperString), any(TypeReference.class))).thenReturn(objectMapperAnswer);

        // when
        List<String> pricing = Arrays.asList(pricing1, pricing2);
        Map<String, String> result = pricingService.getPricingInfo(pricing);

        // then
        String requestUrl = requestUrlCaptor.getValue();
        assertThat(requestUrl, is("http://" + hostname + ":" + port +
                "/" + pricingEndpoint + "?q=" + String.join(",", pricing)));
        assertThat(result, is(nullValue()));
    }
}
