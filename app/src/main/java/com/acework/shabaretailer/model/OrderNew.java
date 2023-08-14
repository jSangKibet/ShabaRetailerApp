package com.acework.shabaretailer.model;

import java.util.List;

public class OrderNew {
    public String id;
    public String orderType;
    public String retailerId;
    public long timestamp;
    public List<OrderItem> orderItems;
    public boolean includeLookbook;
    public String county;
    public String town;
    public String status;

    public int getTotal() {
        int bagTotal = 0;
        for (OrderItem item : orderItems) {
            bagTotal += (item.price * item.quantity);
        }
        int transport = county.equals("Nairobi") ? 250 : 500;
        return bagTotal + transport;
    }
}
