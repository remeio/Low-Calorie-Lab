package com.xumengqi.lclab;

import android.content.SharedPreferences;
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
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class StoreFragment extends Fragment {
    private LinearLayout llStoreWarning, llStoreLoad;
    private BottomSheetLayout bslStoreShoppingCart;
    private CardView cvStoreShoppingCart;
    private RecyclerView rvStoreDish;
    private ImageView ivStoreLoad;
    private SparseArray<Goods> goodsSparseArray = new SparseArray<>();

    private List<Dish> dishList;
    private final int LOADING_DISH_LIST = 101, LOAD_DISH_LIST_SUCCESSFULLY = 102, LOAD_DISH_LIST_UNSUCCESSFULLY = 103, LOAD_DISH_LIST_NULL_DATA = 104;

    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING_DISH_LIST:
                    llStoreLoad.setVisibility(View.VISIBLE);
                    llStoreWarning.setVisibility(View.GONE);
                    ivStoreLoad.setVisibility(View.VISIBLE);
                    ivStoreLoad.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.loading));
                    bslStoreShoppingCart.setVisibility(View.GONE);
                    break;
                /* 使用较慢的加载方式，即获取整个列表后设置布局 */
                case LOAD_DISH_LIST_SUCCESSFULLY:
                    /* 给用户反馈，加载成功 */
                    llStoreLoad.setVisibility(View.GONE);
                    bslStoreShoppingCart.setVisibility(View.VISIBLE);
                    cvStoreShoppingCart.setVisibility(View.VISIBLE);
                    /* 设置适配器 */
                    DishAdapter dishAdapter = new DishAdapter(getView(), dishList, goodsSparseArray);
                    rvStoreDish.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                    break;
                case LOAD_DISH_LIST_UNSUCCESSFULLY:
                    /* 给用户反馈，加载失败 */
                    llStoreLoad.setVisibility(View.VISIBLE);
                    ivStoreLoad.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.load_failed));
                    ivStoreLoad.setVisibility(View.VISIBLE);
                    llStoreWarning.setVisibility(View.VISIBLE);
                    bslStoreShoppingCart.setVisibility(View.GONE);
                    break;
                case LOAD_DISH_LIST_NULL_DATA:
                    /* 给用户反馈，空空如也 */
                    llStoreLoad.setVisibility(View.VISIBLE);
                    ivStoreLoad.setVisibility(View.VISIBLE);
                    ivStoreLoad.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_more));
                    llStoreWarning.setVisibility(View.GONE);
                    bslStoreShoppingCart.setVisibility(View.GONE);
                    break;
                    default:
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store, container, false);
        /* 模块一：设置商城菜品列表 */
        /* 1.初始化菜品列表 */
        initializeDishList();
        /* 2.设置列表管理器 */
        rvStoreDish = view.findViewById(R.id.rv_store_dish);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        rvStoreDish.setLayoutManager(gridLayoutManager);
        /* 3.设置适配器，这一步放到Handler里面 */

        /* 模块二：设置购物车商品列表 */
        /* 打开购物车 */
        cvStoreShoppingCart = view.findViewById(R.id.cv_store_shopping_cart);
        cvStoreShoppingCart.setVisibility(View.GONE);
        cvStoreShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 子模块一：设置购物车能显示 */
                /* 设置底部弹出的布局 */
                BottomSheetLayout bslStoreShoppingCart = view.findViewById(R.id.bsl_store_shopping_cart);
                View viewShoppingCart = LayoutInflater.from(view.getContext()).inflate(R.layout.item_shopping_cart, bslStoreShoppingCart, false);
                bslStoreShoppingCart.showWithSheetView(viewShoppingCart);
                /* 再次点击，或点击空白处，或下滑关闭购物车 */
                bslStoreShoppingCart.dismissSheet();

                /* 子模块二：设置购物车内容 */
                /* 设置列表管理器及适配器，刷新步骤为先数据刷新，再布局刷新 */
                RecyclerView rvShoppingCartGoods = viewShoppingCart.findViewById(R.id.rv_shopping_cart_goods);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(viewShoppingCart.getContext(), 1);
                rvShoppingCartGoods.setLayoutManager(gridLayoutManager);
                final GoodsAdapter goodsAdapter = new GoodsAdapter(view, goodsSparseArray);
                rvShoppingCartGoods.setAdapter(goodsAdapter);
                /* 到这一步数据刷新好了，要进行布局的刷新，即总价格和给用户的反馈 */
                goodsAdapter.doSomethingThenUpdate();

                /*  子模块三：设置组件功能 */
                /* 设置清空购物车 */
                TextView tvShoppingCartCleanAll = view.findViewById(R.id.tv_shopping_cart_clean_all);
                tvShoppingCartCleanAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* 清空购物车列表 */
                        goodsSparseArray.clear();
                        /* 到这一步数据刷新好了，要进行布局的刷新，即总价格和给用户的反馈 */
                        goodsAdapter.doSomethingThenUpdate();
                    }
                });
            }
        });

        /* 模块三：给用户反馈，是对模块一的补充 */
        bslStoreShoppingCart = view.findViewById(R.id.bsl_store_shopping_cart);
        ivStoreLoad = view.findViewById(R.id.iv_store_load);
        /* 因断开网络，无法显示菜品，点击刷新 */
        llStoreWarning = view.findViewById(R.id.ll_store_warning);
        llStoreWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDishList();
            }
        });
        llStoreLoad = view.findViewById(R.id.ll_store_load);

        /* 模块四：设置分类菜品的跳转功能 */
        /* 推荐个数为三个 */
        final int classZero = 0, classOne = 3, classTwo = 14, classThree = 19, classFour = 31;
        TextView tvStoreClassZero = view.findViewById(R.id.tv_store_class_zero);
        tvStoreClassZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPosition(gridLayoutManager, rvStoreDish, classZero);
            }
        });
        TextView tvStoreClassOne = view.findViewById(R.id.tv_store_class_one);
        tvStoreClassOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPosition(gridLayoutManager, rvStoreDish, classOne);
            }
        });
        TextView tvStoreClassTwo = view.findViewById(R.id.tv_store_class_two);
        tvStoreClassTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPosition(gridLayoutManager, rvStoreDish, classTwo);
            }
        });
        TextView tvStoreClassThree = view.findViewById(R.id.tv_store_class_three);
        tvStoreClassThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPosition(gridLayoutManager, rvStoreDish, classThree);
            }
        });
        TextView tvStoreClassFour = view.findViewById(R.id.tv_store_class_four);
        tvStoreClassFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPosition(gridLayoutManager, rvStoreDish, classFour);
            }
        });

        /* 模块五：结算功能 */
        Button btnStorePay = view.findViewById(R.id.btn_store_pay);
        btnStorePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    /**
     * 初始化菜品列表
     */
    public void initializeDishList() {
        /* 网络操作必须在线程中进行 */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* 发送正在加载信息，目的是给用户反馈 */
                handler.sendMessage(theMessage(LOADING_DISH_LIST));
                /* 加载数据 */
                /* 用于解决OOM问题：所有图片都存在内存中，所以要及时释放内存 */
                if (dishList != null) {
                    dishList.clear();
                }
                dishList = new ArrayList<>();
                DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
                databaseConnector.connectToDatabase();
                dishList = databaseConnector.getAllDish();
                /* 利用推荐算法更新dishList */
                SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("lc_lab_user_information", MODE_PRIVATE);
                String account =  sharedPreferences.getString("account",null);
                String password =  sharedPreferences.getString("password",null);
                User user = databaseConnector.getUserByAccount(account, password);
                List<Dish> dishListRecommend = getRecommendedDish(user, dishList);
                if (dishListRecommend != null) {
                    for (Dish dish: dishListRecommend) {
                        dishList.add(0, dish);
                    }
                }
                databaseConnector.closeDatabase();
                /* 根据数据来确认如何响应，并发送相应信息 */
                if (dishList.size() != 0) {
                    /* 通知加载成功 */
                    handler.sendMessage(theMessage(LOAD_DISH_LIST_SUCCESSFULLY));
                }
                else {
                    if (LcLabToolkit.isConnectingForNetwork(Objects.requireNonNull(getContext()))) {
                        /* 内部数据库为空，属于数据库问题 */
                        handler.sendMessage(theMessage(LOAD_DISH_LIST_NULL_DATA));
                    }
                    else {
                        /* 断开网络，故加载失败 */
                        handler.sendMessage(theMessage(LOAD_DISH_LIST_UNSUCCESSFULLY));
                    }
                }
            }
        };
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                1,1,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(runnable);
        threadPool.shutdown();

    }

    /**
     * 随机推荐算法
     * @param user 用户
     * @param dishList 菜品列表
     * @return 推荐的几个菜品
     */
    private List<Dish> getRecommendedDish(User user, List<Dish> dishList) {
        String goal = (user == null ? null : user.getDietaryTarget());
        String fatLoss = "减脂", muscleGain = "增肌";
        String fatReductionMeal = "减脂餐", salad = "沙拉", muscleMeal = "增肌餐", pasta = "意面轻料理";
        /* 最终结果 */
        List<Dish> recommendedDishList = new ArrayList<>();
        /* 满足条件的 */
        List<Dish> dishListTemp = new ArrayList<>();
        if (goal == null) {
            dishListTemp.addAll(dishList);
        } else if (goal.equals(fatLoss)) {
            for (Dish dish: dishList) {
                if (dish.getCategory().equals(fatReductionMeal) || dish.getCategory().equals(salad)) {
                    dishListTemp.add(dish);
                }
            }
        } else if (goal.equals(muscleGain)) {
            for (Dish dish: dishList) {
                if (dish.getCategory().equals(muscleMeal) || dish.getCategory().equals(pasta)) {
                    dishListTemp.add(dish);
                }
            }
        }
        /* 如果总list的size小于推荐个数，就没必要推荐 */
        int recommendedNumber = 3;
        if (dishListTemp.size() <= recommendedNumber) {
            return null;
        }
        else {
            /* 随机生成0到size()-1，中的数，要三个不一样的，注意下标越界问题 */
            int[] indexArray = new int[] {0, 0, 0};
            int indexMin = 0;
            int indexMax = dishListTemp.size() - 1;
            do {
                indexArray[0] = (int)((Math.random() * (indexMax - indexMin + 1)) + indexMin);
                indexArray[1] = (int)((Math.random() * (indexMax - indexMin + 1)) + indexMin);
                indexArray[2] = (int)((Math.random() * (indexMax - indexMin + 1)) + indexMin);
            } while (indexArray[0] == indexArray[1] || indexArray[0] == indexArray[2] || indexArray[1] == indexArray[2]);
            for (int j : indexArray) {
                recommendedDishList.add(dishListTemp.get(j));
            }
            /* 返回推荐后后的列表 */
            return recommendedDishList;
        }
    }

    /**
     * 移动RecyclerView中的项到指定位置
     * @param gridLayoutManager 布局管理
     * @param mRecyclerView 列表
     * @param n 要移动到的项的位置
     */
    public static void moveToPosition(GridLayoutManager gridLayoutManager, RecyclerView mRecyclerView, int n) {
        mRecyclerView.scrollToPosition(n);
        gridLayoutManager.scrollToPositionWithOffset(n, 0);
    }

    /**
     * 便于发送信息
     * @param what 信息内容
     * @return 信息
     */
    private Message theMessage(int what) {
        Message message = new Message();
        message.what = what;
        return message;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        /* 用于检查用户数据是否被更新，如果用户数据被更新，则刷新菜品数据 */
        if (!hidden && LcLabToolkit.isReadyToUpdate()) {
            initializeDishList();
            LcLabToolkit.setReadyToUpdate(false);
            LcLabToolkit.showToastHint(getContext(), "正在为您重新推荐", R.drawable.hourglass);
        }
    }
}