package com.acework.shabaretailer.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int orderType;
    private final List<Item> items;

    public Cart(){
        orderType=0;
        items=new ArrayList<>();
    }

    public int getOrderType() {
        return orderType;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
}
