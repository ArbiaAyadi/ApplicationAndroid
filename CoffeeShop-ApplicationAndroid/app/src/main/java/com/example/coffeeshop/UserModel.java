package com.example.coffeeshop;

public class UserModel {
    private String email;
    private String role;


    public UserModel() {

    }


    public UserModel(String email, String role) {
        this.email = email;
        this.role = role;
    }


    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
