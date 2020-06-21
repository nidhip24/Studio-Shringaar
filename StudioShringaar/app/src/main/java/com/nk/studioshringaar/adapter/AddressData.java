package com.nk.studioshringaar.adapter;

public class AddressData {

    String id, name, address, phone, city, state, country;
    Boolean checked;

    public AddressData(String id, String name, String address, String phone){
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        checked = false;
    }

    public AddressData(String id, String name, String address, String phone, String city, String state, String country){
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.country = country;
        checked = false;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }
}
