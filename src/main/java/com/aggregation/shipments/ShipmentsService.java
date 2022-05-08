package com.aggregation.shipments;

import com.aggregation.configuration.ApiProperties;
import com.aggregation.okhttp.OkHttpCallManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ShipmentsService {

    private final ObjectMapper objectMapper;
    private final ApiProperties apiProperties;
    private final OkHttpCallManager okHttpCallManager;

    @Async
    public CompletableFuture<Map<String, List<String>>> getShipmentInfo(List<String> shipments) {
        Map<String, List<String>> answer = null;

        if (!shipments.isEmpty()) {
            String responseBody = okHttpCallManager.call("http://" + apiProperties.getHostname() + ":" + apiProperties.getPort() +
                    "/" + apiProperties.getShipmentsEndpoint() + "?q=" + String.join(",", shipments));

            try {
                answer = parseResult(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return CompletableFuture.completedFuture(answer);
    }

    private Map<String, List<String>> parseResult(String responseBody) throws IOException {
        Map<String, List<String>> answer;
        answer = responseBody != null ? objectMapper.readValue(responseBody, new TypeReference<HashMap<String, List<String>>>() {}) : Collections.emptyMap();

        if (answer != null && answer.containsKey("message")) {
            answer = null;
        }

        return answer;
    }
}
