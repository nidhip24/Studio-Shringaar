package com.nk.studioshringaar.adapter;

public class PaymentData {

    private String id, orderID, title, amount, status, timestamp, img, order_status;

    public PaymentData(String id, String orderID, String title, String amount, String status, String timestamp, String img){
        this.id = id;
        this.orderID = orderID;
        this.title = title;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
        this.img = img;
    }

    public PaymentData(String id, String orderID, String title, String amount, String status, String order_status, String timestamp, String img){
        this.id = id;
        this.orderID = orderID;
        this.title = title;
        this.amount = amount;
        this.status = status;
        this.order_status = order_status;
        this.timestamp = timestamp;
        this.img = img;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getImg() {
        return img;
    }

    public String getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
