package com.xumengqi.lclab;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {
    private LinearLayout ll_store_warning, ll_store_load;
    private BottomSheetLayout bsl_store_shopping_cart;
    private CardView cv_store_shopping_cart;
    private RecyclerView rv_store_dish;
    private ImageView iv_store_load;
    private SparseArray<Goods> goodsSparseArray = new SparseArray<>();

    private List<Dish> dishList;
    private final int LOADING_DISH_LIST = 101, LOAD_DISH_LIST_SUCCESSFULLY = 102, LOAD_DISH_LIST_UNSUCCESSFULLY = 103, LOAD_DISH_LIST_NULL_DATA = 104;

    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING_DISH_LIST:
                    ll_store_load.setVisibility(View.VISIBLE);
                    ll_store_warning.setVisibility(View.GONE);
                    iv_store_load.setVisibility(View.VISIBLE);
                    iv_store_load.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.loading));
                    bsl_store_shopping_cart.setVisibility(View.GONE);
                    break;
                /* 使用较慢的加载方式，即获取整个列表后设置布局 */
                case LOAD_DISH_LIST_SUCCESSFULLY:
                    /* 给用户反馈，加载成功 */
                    ll_store_load.setVisibility(View.GONE);
                    bsl_store_shopping_cart.setVisibility(View.VISIBLE);
                    cv_store_shopping_cart.setVisibility(View.VISIBLE);
                    /* 设置适配器 */
                    DishAdapter dishAdapter = new DishAdapter(getView(), dishList, goodsSparseArray);
                    rv_store_dish.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                    break;
                case LOAD_DISH_LIST_UNSUCCESSFULLY:
                    /* 给用户反馈，加载失败 */
                    ll_store_load.setVisibility(View.VISIBLE);
                    iv_store_load.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.load_failed));
                    iv_store_load.setVisibility(View.VISIBLE);
                    ll_store_warning.setVisibility(View.VISIBLE);
                    bsl_store_shopping_cart.setVisibility(View.GONE);
                    break;
                case LOAD_DISH_LIST_NULL_DATA:
                    /* 给用户反馈，空空如也 */
                    ll_store_load.setVisibility(View.VISIBLE);
                    iv_store_load.setVisibility(View.VISIBLE);
                    iv_store_load.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_more));
                    ll_store_warning.setVisibility(View.GONE);
                    bsl_store_shopping_cart.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 加载帧视图 */
        final View view = inflater.inflate(R.layout.fragment_store, container, false);

        /* 模块一：设置商城菜品列表 */
        /* 1.初始化菜品列表 */
        initializeDishList();
        /* 2.设置列表管理器 */
        rv_store_dish = view.findViewById(R.id.rv_store_dish);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        rv_store_dish.setLayoutManager(gridLayoutManager);
        /* 3.设置适配器，这一步放到Handler里面 */

        /* 模块二：设置购物车菜品列表 */
        /* 打开购物车，每次点击都是一次新的执行 */
        cv_store_shopping_cart = view.findViewById(R.id.cv_store_shopping_cart);
        cv_store_shopping_cart.setVisibility(View.GONE);
        cv_store_shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 子模块一：设置购物车能显示 */
                /* 设置底部弹出的布局 */
                BottomSheetLayout bsl_store_shopping_cart = view.findViewById(R.id.bsl_store_shopping_cart);
                View viewShoppingCart = LayoutInflater.from(view.getContext()).inflate(R.layout.item_shopping_cart, bsl_store_shopping_cart, false);
                bsl_store_shopping_cart.showWithSheetView(viewShoppingCart);
                /* 再次点击，或点击空白处，或下滑关闭购物车 */
                bsl_store_shopping_cart.dismissSheet();

                /* 子模块二：设置购物车内容 */
                /* 设置列表管理器及适配器，刷新步骤为先数据刷新，再布局刷新 */
                RecyclerView rv_shopping_cart_goods = viewShoppingCart.findViewById(R.id.rv_shopping_cart_goods);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(viewShoppingCart.getContext(), 1);
                rv_shopping_cart_goods.setLayoutManager(gridLayoutManager);
                final GoodsAdapter goodsAdapter = new GoodsAdapter(view, goodsSparseArray);
                rv_shopping_cart_goods.setAdapter(goodsAdapter);
                /* 到这一步数据刷新好了，要进行布局的刷新，即总价格和给用户的反馈 */
                goodsAdapter.DoSomethingThenUpdate();

                /*  子模块三：设置组件功能 */
                /* 设置清空购物车 */
                TextView tv_shopping_cart_clean_all = view.findViewById(R.id.tv_shopping_cart_clean_all);
                tv_shopping_cart_clean_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* 清空购物车列表 */
                        goodsSparseArray.clear();
                        /* 到这一步数据刷新好了，要进行布局的刷新，即总价格和给用户的反馈 */
                        goodsAdapter.DoSomethingThenUpdate();
                    }
                });
            }
        });

        /* 模块三：给用户反馈，是对模块一的补充 */
        bsl_store_shopping_cart = view.findViewById(R.id.bsl_store_shopping_cart);
        iv_store_load = view.findViewById(R.id.iv_store_load);
        /* 因断开网络，无法显示菜品，点击刷新 */
        ll_store_warning = view.findViewById(R.id.ll_store_warning);
        ll_store_warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDishList();
            }
        });
        ll_store_load = view.findViewById(R.id.ll_store_load);

        /* 模块四：设置分类菜品的跳转功能 */
        final int CLASS_ONE = 0, CLASS_TWO = 11, CLASS_THREE = 16, CLASS_FOUR = 28;
        TextView tv_store_class_one = view.findViewById(R.id.tv_store_class_one);
        tv_store_class_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToPosition(gridLayoutManager, rv_store_dish, CLASS_ONE);
            }
        });
        TextView tv_store_class_two = view.findViewById(R.id.tv_store_class_two);
        tv_store_class_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToPosition(gridLayoutManager, rv_store_dish, CLASS_TWO);
            }
        });
        TextView tv_store_class_three = view.findViewById(R.id.tv_store_class_three);
        tv_store_class_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToPosition(gridLayoutManager, rv_store_dish, CLASS_THREE);
            }
        });
        TextView tv_store_class_four = view.findViewById(R.id.tv_store_class_four);
        tv_store_class_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToPosition(gridLayoutManager, rv_store_dish, CLASS_FOUR);
            }
        });

        /* 模块五：结算功能 */
        Button btn_store_pay = view.findViewById(R.id.btn_store_pay);
        btn_store_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    /** 初始化菜品列表 */
    public void initializeDishList() {
        /* 网络操作必须在线程中进行 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                /* 发送正在加载信息，目的是给用户反馈 */
                handler.sendMessage(theMessage(LOADING_DISH_LIST));
                /* 加载数据 */
                dishList = new ArrayList<>();
                DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
                databaseConnector.connectToDatabase();
                dishList = databaseConnector.getAllDish();
                databaseConnector.closeDatabase();
                /* 根据数据来确认如何响应，并发送相应信息 */
                if (dishList.size() != 0) {
                    /* 通知加载成功 */
                    handler.sendMessage(theMessage(LOAD_DISH_LIST_SUCCESSFULLY));
                }
                else {
                    NetworkChecker networkChecker = new NetworkChecker(getContext());
                    if (networkChecker.isConnecting()) {
                        /* 内部数据库为空，属于数据库问题 */
                        handler.sendMessage(theMessage(LOAD_DISH_LIST_NULL_DATA));
                    }
                    else {
                        /* 断开网络，故加载失败 */
                        handler.sendMessage(theMessage(LOAD_DISH_LIST_UNSUCCESSFULLY));
                    }
                }
            }
        }).start();
    }

    /* 移动RecyclerView中的项到指定位置 */
    public static void MoveToPosition(GridLayoutManager gridLayoutManager, RecyclerView mRecyclerView, int n) {
        mRecyclerView.scrollToPosition(n);
        gridLayoutManager.scrollToPositionWithOffset(n, 0);
    }
    private Message theMessage(int what) {
        Message message = new Message();
        message.what = what;
        return message;
    }
}
