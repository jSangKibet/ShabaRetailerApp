package com.acework.shabaretailer.model;

public class Retailer {
    private String name, businessName, telephone, email, county, street, password;
    public Retailer(){}

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

    public void setName(String name) {
        this.name = name;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
