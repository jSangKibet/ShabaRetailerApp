package com.acework.shabaretailer.model;

import java.util.List;

public class Order {
    private String id;
    private Retailer retailer;
    private long timestamp;
    private String status;
    private List<Item> orderItems;
    private int estimatedTotal;
    private int estimatedTransportCost;
    private int finalTotal;

    public Order(String id, Retailer retailer, long timestamp, String status, List<Item> orderItems, int estimatedTotal, int estimatedTransportCost, int finalTotal) {
        this.id = id;
        this.retailer = retailer;
        this.timestamp = timestamp;
        this.status = status;
        this.orderItems = orderItems;
        this.estimatedTotal = estimatedTotal;
        this.estimatedTransportCost = estimatedTransportCost;
        this.finalTotal = finalTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Item> orderItems) {
        this.orderItems = orderItems;
    }

    public int getEstimatedTotal() {
        return estimatedTotal;
    }

    public void setEstimatedTotal(int estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public int getEstimatedTransportCost() {
        return estimatedTransportCost;
    }

    public void setEstimatedTransportCost(int estimatedTransportCost) {
        this.estimatedTransportCost = estimatedTransportCost;
    }

    public int getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(int finalTotal) {
        this.finalTotal = finalTotal;
    }
}
