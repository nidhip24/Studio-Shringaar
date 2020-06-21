package com.nk.studioshringaar.ui.cart;

import java.util.Map;

public class OrderItem {

    private String cat, pid, qty, size, name, price, img;

    public OrderItem(String pid, String qty, String size, String cat, String name, String price, String img) {
        this.cat = cat;
        this.pid = pid;
        this.qty = qty;
        this.size = size;
        this.name = name;
        this.price = price;
        this.img = img;
    }

    public OrderItem (Map<String, String> map) {
        this.cat = map.get("cat");
        this.pid = map.get("pid");
        this.qty = map.get("qty");
        this.size = map.get("size");
        this.name = map.get("name");
        this.price = map.get("price");
        this.img = map.get("img");
    }

    public String getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getCat() {
        return cat;
    }

    public String getSize() {
        return size;
    }

    public String getQty() {
        return qty;
    }

    public String getPid() {
        return pid;
    }
}
