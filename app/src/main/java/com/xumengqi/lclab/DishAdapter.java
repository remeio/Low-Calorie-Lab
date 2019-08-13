package com.xumengqi.lclab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Objects;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {
    private View view;
    private List<Dish> dishList;
    private SparseArray<Goods> goodsSparseArray;

    /** 构造方法，传入视图是为了可能使用上下文和视图 */
    DishAdapter(View view, List<Dish> dishList, SparseArray<Goods> goodsSparseArray) {
        this.view = view;
        this.dishList = dishList;
        this.goodsSparseArray = goodsSparseArray;
    }

    /** 获取组件 */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cvDish;
        private ImageView ivDish, ivDishRecommend;
        private TextView tvDishName, tvDishPrice, tvDishCategory, tvDishCalorie;
        private ImageButton ibDishAddDish;
        ViewHolder(View view) {
            super(view);
            cvDish = view.findViewById(R.id.cv_dish);
            ivDish = view.findViewById(R.id.iv_dish);
            tvDishName = view.findViewById(R.id.tv_dish_name);
            tvDishPrice = view.findViewById(R.id.tv_dish_price);
            tvDishCategory = view.findViewById(R.id.tv_dish_category);
            tvDishCalorie = view.findViewById(R.id.tv_dish_calorie);
            ibDishAddDish = view.findViewById(R.id.ib_dish_add_dish);
            ivDishRecommend = view.findViewById(R.id.iv_dish_recommend);
        }
    }

    /** 绑定布局与项 */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* 绑定项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dish, viewGroup, false);
        return new ViewHolder(view);
    }

    /** 绑定布局与数据 */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        /* 1.获取当前元素 */
        final Dish dish = dishList.get(i);
        /* 2.设置组件的内容 */
        viewHolder.tvDishName.setText(dish.getName());
        int recommendedNumber = 3;
        if (i < recommendedNumber) {
            viewHolder.ivDishRecommend.setVisibility(View.VISIBLE);
        }
        viewHolder.tvDishPrice.setText(("¥" + dish.getPrice()));
        viewHolder.tvDishCategory.setText(dish.getCategory());
        viewHolder.tvDishCalorie.setText((dish.getCalorie() + "千卡"));
        /* 图片加载优化 */
        if (LcLabToolkit.isCacheNotRam()) {
            viewHolder.ivDish.setImageResource(R.drawable.loading_picture);
            String drawableName = "food_picture" + dish.getId();
            int resId = view.getContext().getResources().getIdentifier(drawableName , "drawable", Objects.requireNonNull(view.getContext()).getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(view.getContext().getResources(), resId);
            if (bitmap != null) {
                Glide.with(view)
                        .load(resId)
                        .into(viewHolder.ivDish);
            }
            else {
                String url = "http://106.15.39.96/media/food_information_table_images/";
                Glide.with(view)
                        .load(url + dish.getName() + ".jpg")
                        .apply(new RequestOptions().error(R.drawable.load_picture_failed))
                        .into(viewHolder.ivDish);
            }
        }
        else {
            viewHolder.ivDish.setImageBitmap(dish.getPicture());
        }

        /* 3.设置组件的功能 */
        /* 将被点击菜品加入购物车 */
        viewHolder.ibDishAddDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 数据的获取 */
                Bitmap bitmap = dish.getPicture();
                /* 已添加过的菜品，故数量加一即可 */
                if (goodsSparseArray.get(dish.getId()) != null) {
                    goodsSparseArray.get(dish.getId()).addCount();
                }
                /* 从未添加过的新菜品，故需要添加 */
                else {
                    goodsSparseArray.put(dish.getId(), new Goods(dish.getId(), dish.getName(), bitmap, dish.getPrice(), 1));
                }
                /* 更新总价格, 在更新完购物车数据后执行 */
                updatePrice();
            }
        });
        /* 点击菜品进入详情页 */
        viewHolder.cvDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DishDetailActivity.class);
                intent.putExtra("id_of_dish", dish.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    /** 用于解决对某一项进行操作，其他项会改变的问题 */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /** 添加菜品后要进行总价格的更新 */
    private void updatePrice() {
        TextView tvStorePrice = view.findViewById(R.id.tv_store_price);
        double price = 0;
        for (int i = 0; i < goodsSparseArray.size(); i++) {
            Goods goods = goodsSparseArray.valueAt(i);
            price += goods.getPrice() * goods.getCount();
        }
        tvStorePrice.setText(("¥" + price));
    }
}