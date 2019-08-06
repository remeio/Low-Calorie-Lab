package com.xumengqi.lclab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class DishDetailActivity extends BaseActivity {

    private ImageView ivDishDetailPicture, ivDishDetailLoad;
    private TextView tvDishDetailName, tvDishDetailCalorie, tvDishDetailCategory, tvDishDetailDietaryFiber, tvDishDetailCarbohydrate, tvDishDetailFat, tvDishDetailIngredient, tvDishDetailNotes, tvDishDetailProtein, tvDishDetailPrice;
    private LinearLayout llDishDetail;
    private ProgressBar pbDishDetailLoad;

    private Dish dish;
    private final int LOAD_DISH_DETAIL_SUCCESSFULLY = 101, LOAD_DISH_DETAIL_UNSUCCESSFULLY = 102, LOAD_DISH_DETAIL_NULL_DATA = 103;
    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_DISH_DETAIL_SUCCESSFULLY:
                    /* 给用户反馈，通知加载成功 */
                    pbDishDetailLoad.setVisibility(View.GONE);
                    ivDishDetailLoad.setVisibility(View.GONE);
                    llDishDetail.setVisibility(View.VISIBLE);
                    /* 设置组件的内容 */
                    tvDishDetailName.setText(dish.getName());
                    ivDishDetailPicture.setImageBitmap(dish.getPicture());
                    tvDishDetailCalorie.setText(("卡路里：" + dish.getCalorie() + "千卡"));
                    tvDishDetailCarbohydrate.setText(("碳水化合物：" + dish.getCarbohydrate() + "克"));
                    tvDishDetailFat.setText(("脂肪：" + dish.getFat() + "克"));
                    tvDishDetailProtein.setText(("蛋白质：" + dish.getProtein() + "克"));
                    tvDishDetailDietaryFiber.setText(("膳食纤维：" + dish.getDietaryFiber() + "克"));
                    tvDishDetailIngredient.setText(("主要成分：" + dish.getIngredient()));
                    tvDishDetailCategory.setText(("类别：" + dish.getCategory()));
                    tvDishDetailPrice.setText(("价格：" + dish.getPrice() + "元"));
                    tvDishDetailNotes.setText((dish.getNotes()));
                    break;
                case LOAD_DISH_DETAIL_UNSUCCESSFULLY:
                    /* 给用户反馈，通知加载失败 */
                    pbDishDetailLoad.setVisibility(View.GONE);
                    ivDishDetailLoad.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.load_failed));
                    ivDishDetailLoad.setVisibility(View.VISIBLE);
                    tvDishDetailName.setText("加载失败");
                    break;
                case LOAD_DISH_DETAIL_NULL_DATA:
                    /* 给用户反馈，通知空空如也 */
                    pbDishDetailLoad.setVisibility(View.GONE);
                    ivDishDetailLoad.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_more));
                    ivDishDetailLoad.setVisibility(View.VISIBLE);
                    tvDishDetailName.setText("系统没有找到");
                    break;
                    default:
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        tvDishDetailName = findViewById(R.id.tv_dish_detail_name);
        ivDishDetailPicture = findViewById(R.id.iv_dish_detail_picture);
        tvDishDetailCalorie = findViewById(R.id.tv_dish_detail_calorie);
        tvDishDetailCategory = findViewById(R.id.tv_dish_detail_category);
        tvDishDetailDietaryFiber = findViewById(R.id.tv_dish_detail_dietary_fiber);
        tvDishDetailCarbohydrate = findViewById(R.id.tv_dish_detail_carbohydrate);
        tvDishDetailFat = findViewById(R.id.tv_dish_detail_fat);
        tvDishDetailIngredient = findViewById(R.id.tv_dish_detail_ingredient);
        tvDishDetailNotes = findViewById(R.id.tv_dish_detail_notes);
        tvDishDetailProtein = findViewById(R.id.tv_dish_detail_protein);
        tvDishDetailPrice = findViewById(R.id.tv_dish_detail_price);
        llDishDetail = findViewById(R.id.ll_dish_detail);
        pbDishDetailLoad = findViewById(R.id.pb_dish_detail_load);
        ivDishDetailLoad = findViewById(R.id.iv_dish_detail_load);
        /* 设置返回键的功能 */
        ImageButton ibDishBack = findViewById(R.id.ib_dish_detail_back);
        ibDishBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 结束当前页面 */
                ActivityCollector.finishActivity(DishDetailActivity.this);
            }
        });
        /* 获取上个界面传来的值，默认值为101 */
        Intent intent = getIntent();
        final int idOfDishFromLastIntent = intent.getIntExtra("id_of_dish", 101);
        /* 需要网络，在线程池中进行 */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dish = null;
                DatabaseConnector databaseConnector = new DatabaseConnector(DishDetailActivity.this);
                databaseConnector.connectToDatabase();
                dish = databaseConnector.getDishById(idOfDishFromLastIntent);
                if (dish != null) {
                    Message message = new Message();
                    message.what = LOAD_DISH_DETAIL_SUCCESSFULLY;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    if (LcLabToolkit.isConnectingForNetwork(DishDetailActivity.this)) {
                        /* 内部数据库为空，属于数据库问题 */
                        message.what = LOAD_DISH_DETAIL_NULL_DATA;
                    } else {
                        /* 断开网络，故加载失败 */
                        message.what = LOAD_DISH_DETAIL_UNSUCCESSFULLY;
                    }
                    handler.sendMessage(message);
                }
                databaseConnector.closeDatabase();
            }
        };
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                1,1,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(runnable);
        threadPool.shutdown();
    }
}
