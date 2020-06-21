package com.nk.studioshringaar.data;

public class Promocode {

    private String cat, startDate, endDate, id, name;
    private long discount, maxDiscount;

    public Promocode(String id, String name, String cat, String startDate, String endDate, long discount, long maxDiscount){
        this.name = name;
        this.id = id;
        this.cat = cat;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discount = discount;
        this.maxDiscount = maxDiscount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCat() {
        return cat;
    }

    public long getDiscount() {
        return discount;
    }

    public long getMaxDiscount() {
        return maxDiscount;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }
}
