package com.acework.shabaretailer.model;

import java.util.List;

public class Order {
    private String id;
    private String retailerId;
    private long timestamp;
    private String status;
    private List<Item> orderItems;
    private int estimatedTotal;
    private int estimatedTransportCost;
    private int finalTotal;
    private int finalTransportCost;
    private String county;
    private String street;
    private String type;

    public Order() {
    }

    public Order(String id, String retailerId, long timestamp, String status, List<Item> orderItems, int estimatedTotal, int estimatedTransportCost, int finalTotal, int finalTransportCost, String county, String street, String type) {
        this.id = id;
        this.retailerId = retailerId;
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

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
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

    public int getFinalTransportCost() {
        return finalTransportCost;
    }

    public void setFinalTransportCost(int finalTransportCost) {
        this.finalTransportCost = finalTransportCost;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
