package com.acework.shabaretailer.model;

import java.util.List;

public class Item {
    private String name;
    private int price;
    private String shape, dimensions, weaving, leather, insert, strapLength;
    private String sku;
    private double weight;
    private int quantity;

    public Item() {

    }

    public Item(String name, int price, String shape, String dimensions, String weaving, String leather, String insert, String strapLength, String sku, double weight, int quantity) {
        this.name = name;
        this.price = price;
        this.shape = shape;
        this.dimensions = dimensions;
        this.weaving = weaving;
        this.leather = leather;
        this.insert = insert;
        this.strapLength = strapLength;
        this.sku = sku;
        this.weight = weight;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getShape() {
        return shape;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getWeaving() {
        return weaving;
    }

    public String getLeather() {
        return leather;
    }

    public String getInsert() {
        return insert;
    }

    public String getStrapLength() {
        return strapLength;
    }

    public String getSku() {
        return sku;
    }

    public double getWeight() {
        return weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item cloneItem() {
        return new Item(
                name,
                price,
                shape,
                dimensions,
                weaving,
                leather,
                insert,
                strapLength,
                sku,
                weight,
                quantity);
    }
}
