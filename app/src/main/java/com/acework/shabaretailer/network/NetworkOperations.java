package com.acework.shabaretailer.network;

import androidx.annotation.NonNull;

import com.acework.shabaretailer.network.model.LandedCostRequestBody;
import com.acework.shabaretailer.network.model.ShipmentRequestBody;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkOperations {
    public static void rates(Map<String, String> parameters, NetworkOperationsListener<String> listener) {
        Call<String> call = RetrofitServiceGenerator.endpoints.rates(parameters);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                listener.networkOperationCompleted(true, response.isSuccessful(), response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                listener.networkOperationCompleted(false, false, 0, null);
            }
        });
    }

    public static void landedCost(LandedCostRequestBody landedCostRequestBody, NetworkOperationsListener<String> listener) {
        System.out.println(new Gson().toJson(landedCostRequestBody));
        Call<String> call = RetrofitServiceGenerator.endpoints.landedCost(landedCostRequestBody);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    listener.networkOperationCompleted(true, true, response.code(), response.body());
                } else {
                    String errorMessage = "";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Unknown error";
                        }
                    }
                    listener.networkOperationCompleted(true, false, response.code(), errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                listener.networkOperationCompleted(false, false, 0, null);
            }
        });
    }

    public static void shipment(ShipmentRequestBody shipmentRequestBody, NetworkOperationsListener<String> listener) {
        Call<String> call = RetrofitServiceGenerator.endpoints.shipment(shipmentRequestBody);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                listener.networkOperationCompleted(true, response.isSuccessful(), response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                listener.networkOperationCompleted(false, false, 0, null);
            }
        });
    }
}
