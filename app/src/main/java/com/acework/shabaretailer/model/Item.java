package com.acework.shabaretailer.model;

import java.util.List;

public class Item {
    private String name, type, description, size, material, weaving, colour, strap, strapLength, insert, insertColor;
    private int price, weight;
    private String sku;
    private List<String> features;
    private int quantity;

    public Item() {

    }

    public Item(String name, String type, String description, String size, String material, String weaving, String colour, String strap, String strapLength, String insert, String insertColor, int price, int weight, String sku, List<String> features, int quantity) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.size = size;
        this.material = material;
        this.weaving = weaving;
        this.colour = colour;
        this.strap = strap;
        this.strapLength = strapLength;
        this.insert = insert;
        this.insertColor = insertColor;
        this.price = price;
        this.weight = weight;
        this.sku = sku;
        this.features = features;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }

    public String getWeaving() {
        return weaving;
    }

    public String getColour() {
        return colour;
    }

    public String getStrap() {
        return strap;
    }

    public String getStrapLength() {
        return strapLength;
    }

    public String getInsert() {
        return insert;
    }

    public int getPrice() {
        return price;
    }

    public int getWeight() {
        return weight;
    }

    public String getSku() {
        return sku;
    }

    public List<String> getFeatures() {
        return features;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInsertColor() {
        return insertColor;
    }

    public void setInsertColor(String insertColor) {
        this.insertColor = insertColor;
    }

    public Item cloneItem() {
        return new Item(
                name,
                type,
                description,
                size,
                material,
                weaving,
                colour,
                strap,
                strapLength,
                insert,
                insertColor,
                price,
                weight,
                sku,
                features,
                quantity);
    }

    public Item cloneItem(String insertColor) {
        return new Item(
                name,
                type,
                description,
                size,
                material,
                weaving,
                colour,
                strap,
                strapLength,
                insert,
                insertColor,
                price,
                weight,
                sku,
                features,
                quantity);
    }
}
