package com.acework.shabaretailer.network;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitServiceGenerator {
    private static final String MY_DHL_API_BASE_URL = "https://express.api.dhl.com/mydhlapi/";
    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
        Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json").addHeader("Accept", "application/json").build();
        return chain.proceed(request);
    });

    public static final MyDHLEndpoints endpoints = createService(MyDHLEndpoints.class);

    public static <S> S createService(Class<S> serviceClass) {
        String authToken = Credentials.basic("apZ9zJ0zI2aR4b", "I^1jD^2fL@4gD!7w");
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MY_DHL_API_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor);
            builder.client(httpClient.build());
        }
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
