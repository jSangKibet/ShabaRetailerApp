package com.acework.shabaretailer.model;

public class Retailer {
    private String name, businessName, telephone, county, street, email;
    private int lookbook;

    public Retailer() {
    }

    public Retailer(String name, String businessName, String telephone, String county, String street, String email) {
        this.name = name;
        this.businessName = businessName;
        this.telephone = telephone;
        this.county = county;
        this.street = street;
        this.email = email;
        this.lookbook = 0;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLookbook() {
        return lookbook;
    }
}
