package com.xumengqi.lclab;

import android.graphics.Bitmap;

public class Dish {
    private int id;
    private String name;
    private Bitmap picture;
    private double price;
    private double calorie_k_calorie;
    private String category;
    private String ingredient;
    private double carbohydrate_g;
    private double fat_g;
    private double protein_g;
    private double dietary_fiber_g;
    private String notes;

    public Dish(
            int id,
            String name,
            Bitmap picture,
            double price,
            double calorie_k_calorie,
            String category,
            String ingredient,
            double carbohydrate_g,
            double fat_g,
            double protein_g,
            double dietary_fiber_g,
            String notes) {

        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.calorie_k_calorie = calorie_k_calorie;
        this.category = category;
        this.ingredient = ingredient;
        this.carbohydrate_g = carbohydrate_g;
        this.fat_g = fat_g;
        this.protein_g = protein_g;
        this.dietary_fiber_g = dietary_fiber_g;
        this.notes = notes;
    }

    public int getId() {
        return id;
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

    public double getCalorie_k_calorie() {
        return calorie_k_calorie;
    }

    public String getCategory() {
        return category;
    }

    public String getIngredient() {
        return ingredient;
    }

    public double getCarbohydrate_g() {
        return carbohydrate_g;
    }

    public double getFat_g() {
        return fat_g;
    }

    public double getProtein_g() {
        return protein_g;
    }

    public double getDietary_fiber_g() {
        return dietary_fiber_g;
    }

    public String getNotes() {
        return notes;
    }
}

