package com.xumengqi.lclab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private View view;
    private SparseArray<Goods> goodsSparseArray;

    GoodsAdapter(View view, SparseArray<Goods> goodsSparseArray){
        this.view = view;
        this.goodsSparseArray = goodsSparseArray;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvGoodsName, tvGoodsPrice;
        private TextView tvGoodsAmount;
        private ImageView ivGoodsPicture;
        private ImageView ivGoodsAdd, ivGoodsMinus;
        ViewHolder(View view){
            super(view);
            tvGoodsName = view.findViewById(R.id.tv_goods_name);
            tvGoodsPrice = view.findViewById(R.id.tv_goods_price);
            ivGoodsPicture = view.findViewById(R.id.iv_goods_picture);
            tvGoodsAmount = view.findViewById(R.id.tv_goods_amount);
            ivGoodsAdd = view.findViewById(R.id.iv_goods_add);
            ivGoodsMinus = view.findViewById(R.id.iv_goods_minus);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* 绑定模板项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        /* 1.获取当前项 */
        final Goods goods = goodsSparseArray.valueAt(i);
        /* 2.设置组件的内容 */
        viewHolder.tvGoodsName.setText(goods.getName());
        viewHolder.tvGoodsPrice.setText(("¥" + goods.getPrice() * goods.getCount()));
        /* 图片加载优化 */
        if (LcLabToolkit.isCacheNotRam()) {
            viewHolder.ivGoodsPicture.setImageResource(R.drawable.loading_picture);
            String drawableName = "food_picture" + goods.getId();
            int resId = view.getContext().getResources().getIdentifier(drawableName , "drawable", Objects.requireNonNull(view.getContext()).getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(view.getContext().getResources(), resId);
            if (bitmap != null) {
                Glide.with(view)
                        .load(resId)
                        .into(viewHolder.ivGoodsPicture);
            }
            else {
                String url = "http://106.15.39.96/media/food_information_table_images/";
                Glide.with(view)
                        .load(url + goods.getName() + ".jpg")
                        .apply(new RequestOptions().error(R.drawable.load_picture_failed))
                        .into(viewHolder.ivGoodsPicture);
            }
        }
        else {
            viewHolder.ivGoodsPicture.setImageBitmap(goods.getPicture());
        }

        viewHolder.tvGoodsAmount.setText((goods.getCount() + ""));
        /* 3.设置组件的功能 */
        viewHolder.tvGoodsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 此处可设置点击商品名称跳到详细界面 */
            }
        });
        /* 将当前被点击菜品数量加一 */
        viewHolder.ivGoodsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsSparseArray.valueAt(i).addCount();
                doSomethingThenUpdate();
            }
        });
        /* 将当前被点击菜品数量减一 */
        viewHolder.ivGoodsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsSparseArray.valueAt(i).minusCount();
                if (goodsSparseArray.valueAt(i).getCount() == 0) {
                    goodsSparseArray.removeAt(i);
                }
                doSomethingThenUpdate();
            }
        });
        /* 点击菜品进入详情页 */
        viewHolder.ivGoodsPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DishDetailActivity.class);
                intent.putExtra("id_of_dish", goods.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsSparseArray.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /** 更新总价格, 在更新完购物车后执行 */
    private void updatePrice() {
        TextView tvStorePrice = view.findViewById(R.id.tv_store_price);
        double price = 0;
        for (int i = 0; i < goodsSparseArray.size(); i++) {
            Goods goods = goodsSparseArray.valueAt(i);
            price += goods.getPrice() * goods.getCount();
        }
        tvStorePrice.setText(("¥" + price));
    }

    /** 判断当前数据状态，再刷新布局 */
    void doSomethingThenUpdate() {
        RecyclerView rvShoppingCartGoods = view.findViewById(R.id.rv_shopping_cart_goods);
        ImageView ivShoppingCartNull = view.findViewById(R.id.iv_shopping_cart_null);
        /* 给用户反馈，显示空空如也 */
        if (goodsSparseArray.size() == 0) {
            rvShoppingCartGoods.setVisibility(View.GONE);
            ivShoppingCartNull.setVisibility(View.VISIBLE);
        }
        /* 给用户反馈，显示列表 */
        else {
            rvShoppingCartGoods.setVisibility(View.VISIBLE);
            ivShoppingCartNull.setVisibility(View.GONE);
        }
        /* 更新总价格, 在更新完数据后执行 */
        updatePrice();
        /* 刷新布局 */
        notifyDataSetChanged();
    }
}