package com.xumengqi.lclab;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private View view;
    private SparseArray<Goods> goodsSparseArray;

    public GoodsAdapter(View view, SparseArray<Goods> goodsSparseArray){
        this.view = view;
        this.goodsSparseArray = goodsSparseArray;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cv_goods;
        private TextView tv_goods_name, tv_goods_price;
        private TextView tv_goods_amount;
        private ImageView iv_goods_picture;
        private ImageView iv_goods_add, iv_goods_minus;
        public ViewHolder(View view){
            super(view);
            cv_goods = view.findViewById(R.id.cv_goods);
            tv_goods_name = view.findViewById(R.id.tv_goods_name);
            tv_goods_price = view.findViewById(R.id.tv_goods_price);
            iv_goods_picture = view.findViewById(R.id.iv_goods_picture);
            tv_goods_amount = view.findViewById(R.id.tv_goods_amount);
            iv_goods_add = view.findViewById(R.id.iv_goods_add);
            iv_goods_minus = view.findViewById(R.id.iv_goods_minus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /* 绑定模板项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        /* 1.获取当前项 */
        final Goods goods = goodsSparseArray.valueAt(i);
        /* 2.设置组件的内容 */
        viewHolder.tv_goods_name.setText(goods.getName());
        viewHolder.tv_goods_price.setText(("¥" + goods.getPrice() * goods.getCount()));
        viewHolder.iv_goods_picture.setImageBitmap(goods.getPicture());
        viewHolder.tv_goods_amount.setText((goods.getCount() + ""));
        /* 3.设置组件的功能 */
        viewHolder.tv_goods_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        /* 将当前被点击菜品数量加一 */
        viewHolder.iv_goods_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsSparseArray.valueAt(i).addCount();
                DoSomethingThenUpdate();
            }
        });
        /* 将当前被点击菜品数量减一 */
        viewHolder.iv_goods_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsSparseArray.valueAt(i).minusCount();
                if (goodsSparseArray.valueAt(i).getCount() == 0) {
                    goodsSparseArray.removeAt(i);
                }
                DoSomethingThenUpdate();
            }
        });
        /* 点击菜品进入详情页 */
        viewHolder.cv_goods.setOnClickListener(new View.OnClickListener() {
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
    public void updatePrice() {
        TextView tv_store_price = view.findViewById(R.id.tv_store_price);
        double price = 0;
        for (int i = 0; i < goodsSparseArray.size(); i++) {
            Goods goods = goodsSparseArray.valueAt(i);
            price += goods.getPrice() * goods.getCount();
        }
        tv_store_price.setText(("¥" + price));
    }

    /** 判断当前数据状态，再刷新布局 */
    public void DoSomethingThenUpdate() {
        RecyclerView rv_shopping_cart_goods = view.findViewById(R.id.rv_shopping_cart_goods);
        ImageView iv_shopping_cart_null = view.findViewById(R.id.iv_shopping_cart_null);
        /* 给用户反馈，显示空空如也 */
        if (goodsSparseArray.size() == 0) {
            rv_shopping_cart_goods.setVisibility(View.GONE);
            iv_shopping_cart_null.setVisibility(View.VISIBLE);
        }
        /* 给用户反馈，显示列表 */
        else {
            rv_shopping_cart_goods.setVisibility(View.VISIBLE);
            iv_shopping_cart_null.setVisibility(View.GONE);
        }
        /* 更新总价格, 在更新完数据后执行 */
        updatePrice();
        /* 刷新布局 */
        notifyDataSetChanged();
    }

}
