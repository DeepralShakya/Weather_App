package com.example.weatherapp;

public class MyDataModel {
    private String dt;
    private String temp;
    private String imageUrl;

    public MyDataModel(String dt, String temp, String imageUrl) {
        this.dt = dt;
        this.temp = temp;
        this.imageUrl = imageUrl;
    }

    public String getDt() {
        return dt;
    }

    public String getTemp() {
        return temp;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


