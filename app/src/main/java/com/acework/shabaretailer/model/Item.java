package com.acework.shabaretailer.model;

import java.util.List;

public class Item {
    private final String name;
    private final int price;
    private final String shape, dimensions, weaving, leather, insert, strapLength;
    private final List<String> images;
    private final String sku;
    private final double weight;
    private final int quantity;

    public Item(String name, int price, String shape, String dimensions, String weaving, String leather, String insert, String strapLength, List<String> images, String sku, double weight, int quantity) {
        this.name = name;
        this.price = price;
        this.shape = shape;
        this.dimensions = dimensions;
        this.weaving = weaving;
        this.leather = leather;
        this.insert = insert;
        this.strapLength = strapLength;
        this.images = images;
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

    public List<String> getImages() {
        return images;
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
}
