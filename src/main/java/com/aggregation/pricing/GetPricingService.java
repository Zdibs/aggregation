package com.aggregation.pricing;

import com.aggregation.configuration.ApiProperties;
import com.aggregation.okhttp.OkHttpCallManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetPricingService {

    private final OkHttpCallManager okHttpCallManager;
    private final ApiProperties apiProperties;
    private final ObjectMapper objectMapper;

    public Map<String, String> getPricingInfo(List<String> countryCodes) {
        String responseBody = okHttpCallManager.call("http://" + apiProperties.getHostname() + ":" + apiProperties.getPort() +
                "/" + apiProperties.getPricingEndpoint() + "?q=" + String.join(",", countryCodes));

        try {
            return parseResult(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> parseResult(String responseBody) throws IOException {
        Map<String, String> answer;
        answer = responseBody != null ? objectMapper.readValue(responseBody, new TypeReference<HashMap<String, String>>() {}) : Collections.emptyMap();

        if (answer != null && answer.containsKey("message")) {
            answer = null;
        }

        return answer;
    }
}
