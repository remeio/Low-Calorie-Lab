package com.xumengqi.lclab;

import android.graphics.Bitmap;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class Dish {
    private int id;
    private String name;
    private Bitmap picture;
    private double price;
    private double calorie;
    private String category;
    private String ingredient;
    private double carbohydrate;
    private double fat;
    private double protein;
    private double dietaryFiber;
    private String notes;

     Dish(
            int id,
            String name,
            Bitmap picture,
            double price,
            double calorie,
            String category,
            String ingredient,
            double carbohydrate,
            double fat,
            double protein,
            double dietaryFiber,
            String notes) {

        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.calorie = calorie;
        this.category = category;
        this.ingredient = ingredient;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
        this.dietaryFiber = dietaryFiber;
        this.notes = notes;
    }

    int getId() {
        return id;
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

    double getCalorie() {
        return calorie;
    }

    String getCategory() {
        return category;
    }

    String getIngredient() {
        return ingredient;
    }

    double getCarbohydrate() {
        return carbohydrate;
    }

    double getFat() {
        return fat;
    }

    double getProtein() {
        return protein;
    }

    double getDietaryFiber() {
        return dietaryFiber;
    }

    String getNotes() {
        return notes;
    }
}

