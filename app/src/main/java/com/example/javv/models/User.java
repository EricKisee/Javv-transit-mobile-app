package com.example.javv.models;

public class User {
    public String username;
    public String email;
    public String gender;
    public String location;
    public String mode;
    public double lat;
    public double lng;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String gender, String location , String mode , double lat, double lng ) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.location = location;
        this.mode = mode;
        this.lat = lat;
        this.lng = lng;
    }
}
