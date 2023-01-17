package com.acework.shabaretailer.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<Item> items;
    private int orderType;

    public Cart() {
        orderType = -1;
        items = new ArrayList<>();
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public List<Item> getItems() {
        return items;
    }
}
