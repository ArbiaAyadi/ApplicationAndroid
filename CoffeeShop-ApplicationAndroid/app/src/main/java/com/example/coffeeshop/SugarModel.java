package com.example.coffeeshop;

public class SugarModel {
    private String id;
    private String type;
    private int bagCount;

    public SugarModel() {}

    public SugarModel(String type, int bagCount) {
        this.type = type;
        this.bagCount = bagCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBagCount() {
        return bagCount;
    }

    public void setBagCount(int bagCount) {
        this.bagCount = bagCount;
    }

    @Override
    public String toString() {
        return type + " x" + bagCount;  // Returns something like "White x2" or "Brown x1"
    }
}
