package com.example.coffeeshop;

import java.io.Serializable;

public class CoffeeModel implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageBase64;


    private SugarModel sugar;
    private CupsModel cup;

    public CoffeeModel() {}

    public CoffeeModel(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;

    }

    // Getters and Setters
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public SugarModel getSugar() {
        return sugar;
    }

    public void setSugar(SugarModel sugar) {
        this.sugar = sugar;
    }

    public CupsModel getCup() {
        return cup;
    }

    public void setCup(CupsModel cup) {
        this.cup = cup;
    }

    }
