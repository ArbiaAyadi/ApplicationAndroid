package com.example.coffeeshop;
import java.io.Serializable;
public class CupsModel {

    private String id;
    private String name;
    private String description;
    private String size;
    private int stock;

    private String imageBase64;


    public CupsModel() {
    }

    public CupsModel(String name , String description, String size, int stock) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.stock = stock;


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
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}


