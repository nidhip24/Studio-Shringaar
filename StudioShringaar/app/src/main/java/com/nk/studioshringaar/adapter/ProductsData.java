package com.nk.studioshringaar.adapter;


import java.util.List;

public class ProductsData {

    private List<Product> p;
    private String title, id;
    private int layout_type;
    private String img1, img2, img3, img4, timestamp;

    public ProductsData(List<Product> p, String title, int l){
        this.p = p;
        this.title = title;
        layout_type = l;
    }

    public ProductsData(String id, List<Product> p, String title, int l, String timestamp){
        this.id = id;
        this.p = p;
        this.title = title;
        layout_type = l;
        this.timestamp = timestamp;
    }

    public ProductsData(List<Product> p, String title, int l, String img1, String img2, String img3, String img4){
        this.p = p;
        this.title = title;
        layout_type = l;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
    }

    public ProductsData(String id, List<Product> p, String title, int l, String img1, String img2, String img3, String img4, String timestamp){
        this.id = id;
        this.p = p;
        this.title = title;
        layout_type = l;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public void setP(List<Product> p) {
        this.p = p;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg4() {
        return img4;
    }

    public String getImg3() {
        return img3;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg1() {
        return img1;
    }

    public List<Product> getP() {
        return p;
    }

    public String getTitle() {
        return title;
    }

    public int getLayout_type() {
        return layout_type;
    }
}
