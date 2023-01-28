package com.acework.shabaretailer.model;

public class Retailer {
    private String name, businessName, telephone, county, street;

    public Retailer() {
    }

    public Retailer(String name, String businessName, String telephone, String county, String street) {
        this.name = name;
        this.businessName = businessName;
        this.telephone = telephone;
        this.county = county;
        this.street = street;
    }

    public String getName() {
        return name;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCounty() {
        return county;
    }

    public String getStreet() {
        return street;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
