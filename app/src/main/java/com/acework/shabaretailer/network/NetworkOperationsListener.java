package com.acework.shabaretailer.network;

public interface NetworkOperationsListener<T> {
    void networkOperationCompleted(boolean requestStatus, boolean responseStatus, int errorCode, T result);
}
