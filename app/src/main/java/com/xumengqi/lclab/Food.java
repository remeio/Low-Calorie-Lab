package com.xumengqi.lclab;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class Food {
    private String category;
    private String name;
    private String calorie;
    private String carbohydrate;
    private String fat;
    private String protein;
    private String notes;

    Food(String category, String name, String calorie, String carbohydrate, String fat, String protein, String notes) {
        this.category = category;
        this.name = name;
        this.calorie = calorie;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
        this.notes = notes;
    }

    String getCategory() {
        return category;
    }

    String getName() {
        return name;
    }

    String getCalorie() {
        return calorie;
    }

    String getCarbohydrate() {
        return carbohydrate;
    }

    String getFat() {
        return fat;
    }

    String getProtein() {
        return protein;
    }

    String getNotes() {
        return notes;
    }
}