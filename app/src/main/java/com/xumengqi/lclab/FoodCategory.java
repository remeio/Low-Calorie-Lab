package com.xumengqi.lclab;

import android.graphics.Bitmap;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class FoodCategory {
    private Bitmap bitmap;
    private String name;

    FoodCategory(Bitmap bitmap, String name) {
        this.bitmap = bitmap;
        this.name = name;
    }

    Bitmap getBitmap() {
        return bitmap;
    }

    String getName() {
        return name;
    }
}