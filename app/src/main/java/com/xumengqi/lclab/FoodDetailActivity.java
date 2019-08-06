package com.xumengqi.lclab;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class FoodDetailActivity extends BaseActivity {
    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        RecyclerView rvFoodDetail = findViewById(R.id.rv_food_detail);
        Intent intent = getIntent();
        String category = intent.getStringExtra("name_of_category");
        /* 设置当前类 */
        TextView tvFoodDetailName = findViewById(R.id.tv_food_detail_name);
        tvFoodDetailName.setText(category);
        /* 根据分类来初始化食材库列表 */
        initializeFoodDetail(category);
        /* 设置列表管理 */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvFoodDetail.setLayoutManager(gridLayoutManager);
        /* 设置适配器 */
        FoodAdapter foodAdapter = new FoodAdapter(foodList);
        rvFoodDetail.setAdapter(foodAdapter);
        /* 设置返回键的功能 */
        ImageButton ibFoodDetailBack = findViewById(R.id.ib_food_detail_back);
        ibFoodDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 结束当前页面 */
                ActivityCollector.finishActivity(FoodDetailActivity.this);
            }
        });
    }

    public void initializeFoodDetail(String category) {
        foodList = new ArrayList<>();
        foodList = getFoodByCategory(this, category);
    }

    /** 连接数据库后，该函数依据分类读取信息 */
    public List<Food> getFoodByCategory(Context context, String category) {
        String sqlCategory = "蛋类、肉类及制品";
        DatabaseHelper databaseHelper= new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        List<Food> foodList = new ArrayList<>();
        switch (category) {
            case "肉类":
                sqlCategory = "蛋类、肉类及制品";
                break;
            case "奶类":
                sqlCategory = "奶类及制品";
                break;
            case "蔬菜":
                sqlCategory = "蔬果和菌藻";
                break;
            case "主食":
                sqlCategory = "谷薯芋、杂豆、主食";
                break;
            case "豆类":
                sqlCategory = "坚果、大豆及制品";
                break;
            case "饮料":
                sqlCategory = "饮料";
                break;
                default:
        }
        Cursor cursor = sqLiteDatabase.query("lc_lab_food_table", null, "category=?", new String[]{sqlCategory}, null,null, null);
        if (cursor.moveToFirst()) {
            do {
                foodList.add(new Food(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        databaseHelper.close();
        return foodList;
    }
}
