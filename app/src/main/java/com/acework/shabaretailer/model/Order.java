package com.acework.shabaretailer.model;

import java.util.List;

public class Order {
    private String id;
    private String retailerId;
    private Retailer retailer;
    private long timestamp;
    private String status;
    private List<Item> orderItems;
    private int estimatedTotal;
    private int estimatedTransportCost;
    private int finalTotal;
    private int finalTransportCost;
    private String county;
    private String street;
    private int type;
    private int lookbook;

    public Order() {
    }

    public Order(String retailerId, Retailer retailer, long timestamp, String status, List<Item> orderItems, int estimatedTotal, int estimatedTransportCost, int finalTotal, int finalTransportCost, String county, String street, int type, int lookbook) {
        this.retailerId = retailerId;
        this.retailer = retailer;
        this.timestamp = timestamp;
        this.status = status;
        this.orderItems = orderItems;
        this.estimatedTotal = estimatedTotal;
        this.estimatedTransportCost = estimatedTransportCost;
        this.finalTotal = finalTotal;
        this.finalTransportCost = finalTransportCost;
        this.county = county;
        this.street = street;
        this.type = type;
        this.lookbook = lookbook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public int getEstimatedTotal() {
        return estimatedTotal;
    }

    public int getEstimatedTransportCost() {
        return estimatedTransportCost;
    }

    public int getFinalTotal() {
        return finalTotal;
    }

    public int getFinalTransportCost() {
        return finalTransportCost;
    }

    public String getCounty() {
        return county;
    }

    public String getStreet() {
        return street;
    }

    public int getType() {
        return type;
    }

    public int getLookbook() {
        return lookbook;
    }
}
