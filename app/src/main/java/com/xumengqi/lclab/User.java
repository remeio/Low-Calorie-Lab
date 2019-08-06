package com.xumengqi.lclab;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class User {
    private int id;
    private String account;
    private String password;
    private Bitmap picture;
    private double height;
    private double weight;
    private Date birthday;
    private String gender;
    private double waistToHipRatio;
    private String exerciseVolume;
    private String dietaryTarget;
    private String notes;
    User(
            int id,
            String account,
            String password,
            Bitmap picture,
            double height,
            double weight,
            Date birthday,
            String gender,
            double waistToHipRatio,
            String exerciseVolume,
            String dietaryTarget,
            String notes) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.picture = picture;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.gender = gender;
        this.waistToHipRatio = waistToHipRatio;
        this.exerciseVolume = exerciseVolume;
        this.dietaryTarget = dietaryTarget;
        this.notes = notes;
    }

    int getId() {
        return id;
    }

    String getAccount() {
        return account;
    }

    String getPassword() {
        return password;
    }

    Bitmap getPicture() {
        return picture;
    }

    double getHeight() {
        return height;
    }

    double getWeight() {
        return weight;
    }

    Date getBirthday() {
        return birthday;
    }

    String getGender() {
        return gender;
    }

    double getWaistToHipRatio() {
        return waistToHipRatio;
    }

    String getDietaryTarget() {
        return dietaryTarget;
    }

    String getExerciseVolume() {
        return exerciseVolume;
    }

    String getNotes() {
        return notes;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setHeight(double height) {
        this.height = height;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    void setWaistToHipRatio(double waistToHipRatio) {
        this.waistToHipRatio = waistToHipRatio;
    }

    void setExerciseVolume(String exerciseVolume) {
        this.exerciseVolume = exerciseVolume;
    }

    void setDietaryTarget(String dietaryTarget) {
        this.dietaryTarget = dietaryTarget;
    }

    void setNotes(String notes) {
        this.notes = notes;
    }

    void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
