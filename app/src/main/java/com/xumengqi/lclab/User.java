package com.xumengqi.lclab;

import android.graphics.Bitmap;

import java.util.Date;

public class User {
    private int id;
    private String account;
    private String password;
    private Bitmap picture;
    private double height_cm;
    private double weight_kg;
    private Date birthday;
    private String gender;
    private double waist_to_hip_ratio;
    private String exercise_volume;
    private String dietary_target;
    private String notes;
    public User(
            int id,
            String account,
            String password,
            Bitmap picture,
            double height_cm,
            double weight_kg,
            Date birthday,
            String gender,
            double waist_to_hip_ratio,
            String exercise_volume,
            String dietary_target,
            String notes) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.picture = picture;
        this.height_cm = height_cm;
        this.weight_kg = weight_kg;
        this.birthday = birthday;
        this.gender = gender;
        this.waist_to_hip_ratio = waist_to_hip_ratio;
        this.exercise_volume = exercise_volume;
        this.dietary_target = dietary_target;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public double getHeight_cm() {
        return height_cm;
    }

    public double getWeight_kg() {
        return weight_kg;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public double getWaist_to_hip_ratio() {
        return waist_to_hip_ratio;
    }

    public String getDietary_target() {
        return dietary_target;
    }

    public String getExercise_volume() {
        return exercise_volume;
    }

    public String getNotes() {
        return notes;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHeight_cm(double height_cm) {
        this.height_cm = height_cm;
    }

    public void setWeight_kg(double weight_kg) {
        this.weight_kg = weight_kg;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWaist_to_hip_ratio(double waist_to_hip_ratio) {
        this.waist_to_hip_ratio = waist_to_hip_ratio;
    }

    public void setExercise_volume(String exercise_volume) {
        this.exercise_volume = exercise_volume;
    }

    public void setDietary_target(String dietary_target) {
        this.dietary_target = dietary_target;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
