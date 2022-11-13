package com.acework.shabaretailer.model;

public class Retailer {
    private final String name, businessName, telephone, email, county, street, password;

    public Retailer(String name, String businessName, String telephone, String email, String county, String street, String password) {
        this.name = name;
        this.businessName = businessName;
        this.telephone = telephone;
        this.email = email;
        this.county = county;
        this.street = street;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public String getCounty() {
        return county;
    }

    public String getStreet() {
        return street;
    }

    public String getPassword() {
        return password;
    }
}
