package com.xumengqi.lclab;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private List<Food> foodList;

    FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }
  
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName, tvFoodCalorie, tvFoodCarbohydrate, tvFoodFat, tvFoodProtein, tvFoodNotes;
        ViewHolder(View view) {
            super(view);
            tvFoodName = view.findViewById(R.id.tv_food_name);
            tvFoodCalorie = view.findViewById(R.id.tv_food_calorie);
            tvFoodCarbohydrate = view.findViewById(R.id.tv_food_carbohydrate);
            tvFoodFat = view.findViewById(R.id.tv_food_fat);
            tvFoodProtein = view.findViewById(R.id.tv_food_protein);
            tvFoodNotes = view.findViewById(R.id.tv_food_notes);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* 绑定项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        /* 获取当前元素 */
        final Food food = foodList.get(i);
        /* 设置当前视图 */
        viewHolder.tvFoodName.setText(food.getName());
        viewHolder.tvFoodCalorie.setText(("卡路里：" + food.getCalorie() + "千卡"));
        viewHolder.tvFoodCarbohydrate.setText(("碳水化合物：" + food.getCarbohydrate() + "克"));
        viewHolder.tvFoodFat.setText(("脂肪：" + food.getFat() + "克"));
        viewHolder.tvFoodProtein.setText(("蛋白质：" + food.getProtein() + "克"));
        viewHolder.tvFoodNotes.setText(food.getNotes());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
