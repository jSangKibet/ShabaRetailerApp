package com.acework.shabaretailer.model;

public class Retailer {
    private String name, businessName, telephone, county, street, email;

    public Retailer() {
    }

    public Retailer(String name, String businessName, String telephone, String county, String street, String email) {
        this.name = name;
        this.businessName = businessName;
        this.telephone = telephone;
        this.county = county;
        this.street = street;
        this.email=email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
