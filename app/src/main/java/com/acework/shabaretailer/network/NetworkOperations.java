package com.acework.shabaretailer.network;

import androidx.annotation.NonNull;

import com.acework.shabaretailer.network.model.ShipmentRequestBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkOperations {
    public static void rates(Map<String, String> parameters, NetworkOperationsListener<String> listener) {
        Call<String> call = RetrofitServiceGenerator.endpoints.rates(parameters);
        call.enqueue(new Callback<String>() {
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

    public static void shipment(ShipmentRequestBody shipmentRequestBody, NetworkOperationsListener<String> listener) {
        Call<String> call = RetrofitServiceGenerator.endpoints.shipment(shipmentRequestBody);
        call.enqueue(new Callback<String>() {
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
