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
}
