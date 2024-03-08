package com.acework.shabaretailer.network;

import com.acework.shabaretailer.network.model.LandedCostRequestBody;
import com.acework.shabaretailer.network.model.ShipmentRequestBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface MyDHLEndpoints {
    @GET("rates")
    Call<String> rates(@QueryMap Map<String, String> parameters);

    Call<String> landedCost(@Body LandedCostRequestBody landedCostRequestBody);

    @POST("shipments")
    Call<String> shipment(@Body ShipmentRequestBody shipmentRequestBody);
}
