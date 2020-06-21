package com.nk.studioshringaar.adapter;

public class OrderItemData {

    String name, price, id, img, cat, qty, size;

    public OrderItemData(String id, String name, String cat, String price, String qty, String size, String img) {
        this.id = id;
        this.name = name;
        this.cat = cat;
        this.price = price;
        this.qty = qty;
        this.size = size;
        this.img = img;
    }

    public String getQty() {
        return qty;
    }

    public String getSize() {
        return size;
    }

    public String getCat() {
        return cat;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getPrice() {
        return price;
    }
}
