package com.xumengqi.lclab;

import android.graphics.Bitmap;

public class Goods {
    private int id;
    private String name;
    private Bitmap picture;
    private double price;
    private int count = 1;

    public Goods(int id, String name, Bitmap picture, double price, int count) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.count = count;
    }

    public void addCount() {
        count++;
    }

    public void minusCount() {
        if (count > 0) {
            count--;
        }
    }
    public String getName() {
        return name;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
