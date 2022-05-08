package com.aggregation.pricing;

import com.aggregation.configuration.ApiProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ObjectMapper objectMapper;
    private final ApiProperties apiProperties;

    public Map<String, String> getPricingInfo(List<String> countryCodes) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://" + apiProperties.getHostname() + ":" + apiProperties.getPort() +
                        "/" + apiProperties.getPricingEndpoint() + "?q=" + String.join(",", countryCodes))
                .build();

        Call call = client.newCall(request);
        try {
            ResponseBody responseBody = call.execute().body();
            return responseBody != null ? objectMapper.readValue(responseBody.string(), new TypeReference<HashMap<String, String>>(){}) : Collections.emptyMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyMap();
    }
}
