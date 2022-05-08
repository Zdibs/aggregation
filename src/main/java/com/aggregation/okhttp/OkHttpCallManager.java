package com.aggregation.okhttp;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OkHttpCallManager {

    public String call(String requestUrl) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        Call call = client.newCall(request);

        try {
            ResponseBody responseBody = call.execute().body();
            return responseBody != null ? responseBody.string() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
