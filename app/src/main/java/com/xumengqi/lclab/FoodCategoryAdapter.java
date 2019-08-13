package com.xumengqi.lclab;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.ViewHolder> {
    private List<FoodCategory> foodCategoryList;

    FoodCategoryAdapter(List<FoodCategory> foodCategoryList) {
        this.foodCategoryList = foodCategoryList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFoodCategoryPicture;
        private TextView tvFoodCategoryName;
        private CardView cvFoodCategory;

        ViewHolder(View view) {
            super(view);
            ivFoodCategoryPicture = view.findViewById(R.id.iv_food_category_picture);
            tvFoodCategoryName = view.findViewById(R.id.tv_food_category_name);
            cvFoodCategory = view.findViewById(R.id.cv_food_category);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* 绑定项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        /* 获取当前元素 */
        final FoodCategory foodCategory = foodCategoryList.get(i);

        /* 设置当前视图 */
        viewHolder.ivFoodCategoryPicture.setImageBitmap(foodCategory.getBitmap());
        viewHolder.tvFoodCategoryName.setText(foodCategory.getName());

        /* 点击获取这一类的食材 */
        viewHolder.cvFoodCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FoodDetailActivity.class);
                intent.putExtra("name_of_category", foodCategory.getName());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodCategoryList.size();
    }

}