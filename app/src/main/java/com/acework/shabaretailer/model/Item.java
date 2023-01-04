package com.acework.shabaretailer.model;

import java.util.List;

public class Item {
    private String name, type, description, size, material, weaving, colour, strap, strapLength, insert, insertColour;
    private int priceWholesale, priceConsignment, priceShaba, weight;
    private String sku;
    private List<String> features;
    private int quantity;

    public Item() {

    }

    public Item(String name, String type, String description, String size, String material, String weaving, String colour, String strap, String strapLength, String insert, String insertColour, int priceWholesale, int priceConsignment, int priceShaba, int weight, String sku, List<String> features, int quantity) {
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
        this.insertColour = insertColour;
        this.priceWholesale = priceWholesale;
        this.priceConsignment = priceConsignment;
        this.priceShaba = priceShaba;
        this.weight = weight;
        this.sku = sku;
        this.features = features;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getWeaving() {
        return weaving;
    }

    public void setWeaving(String weaving) {
        this.weaving = weaving;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getStrap() {
        return strap;
    }

    public void setStrap(String strap) {
        this.strap = strap;
    }

    public String getStrapLength() {
        return strapLength;
    }

    public void setStrapLength(String strapLength) {
        this.strapLength = strapLength;
    }

    public String getInsert() {
        return insert;
    }

    public void setInsert(String insert) {
        this.insert = insert;
    }

    public int getPriceWholesale() {
        return priceWholesale;
    }

    public void setPriceWholesale(int priceWholesale) {
        this.priceWholesale = priceWholesale;
    }

    public int getPriceConsignment() {
        return priceConsignment;
    }

    public void setPriceConsignment(int priceConsignment) {
        this.priceConsignment = priceConsignment;
    }

    public int getPriceShaba() {
        return priceShaba;
    }

    public void setPriceShaba(int priceShaba) {
        this.priceShaba = priceShaba;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInsertColour() {
        return insertColour;
    }

    public void setInsertColour(String insertColour) {
        this.insertColour = insertColour;
    }

    public Item cloneItemWithZeroQuantity(String insertColor) {
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
                priceWholesale,
                priceConsignment,
                priceShaba,
                weight,
                sku,
                features,
                0);
    }
}
