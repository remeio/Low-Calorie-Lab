package com.xumengqi.lclab;

import android.graphics.Bitmap;

/**
 *
 * @author xumengqi
 * @date 2019/08/04
 */
class Goods {
    private int id;
    private String name;
    private Bitmap picture;
    private double price;
    private int count;

    Goods(int id, String name, Bitmap picture, double price, int count) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.count = count;
    }

    void addCount() {
        count++;
    }

    void minusCount() {
        if (count > 0) {
            count--;
        }
    }
    String getName() {
        return name;
    }

    Bitmap getPicture() {
        return picture;
    }

    double getPrice() {
        return price;
    }

    int getCount() {
        return count;
    }

    int getId() {
        return id;
    }
}