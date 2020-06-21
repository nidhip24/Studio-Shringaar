package com.nk.studioshringaar.adapter;

public class Product {

    private String id, name, price, img, img2, img3, img4, category, description, qty, size, oID;

    private Boolean isDiscount;

    public Product(String id, String name, String price, String img, String img2, String img3, String img4, String category, String description){
        this.id = id;
        this.name = name;
        this. price = price;
        this.img = img;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.category = category;
        this.description = description;
    }

    public Product(String id, String name, String price, String img, String category){
        this.id = id;
        this.name = name;
        this. price = price;
        this.img = img;
        this.category = category;
    }

    public Product(String id, String oID,String name, String price, String img, String category, String qty, String size){
        this.id = id;
        this.oID = oID;
        this.name = name;
        this. price = price;
        this.img = img;
        this.category = category;
        this.qty = qty;
        this.size = size;
        this.isDiscount = true;
    }

    public Product(String id, String name, String cat) {
        this.id = id;
        this.name = name;
        this.category = cat;
    }

    public Boolean getDiscount() {
        return isDiscount;
    }

    public void setDiscount(Boolean discount) {
        isDiscount = discount;
    }

    public String getSize() {
        return size;
    }

    public String getoID() {
        return oID;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg3() {
        return img3;
    }

    public String getImg4() {
        return img4;
    }

    public String getDescription() {
        return description;
    }

    public String getId(){ return id; }

    public String getName(){ return name; }

    public String getPrice(){ return price; }

    public String getImg(){ return img; }

    public String getCategory(){ return category; }
}
