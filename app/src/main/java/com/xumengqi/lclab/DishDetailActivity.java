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

public class DishDetailActivity extends BaseActivity {

    private ImageView iv_dish_detail_picture, iv_dish_detail_load;
    private TextView tv_dish_detail_name, tv_dish_detail_calorie_k_calorie, tv_dish_detail_category, tv_dish_detail_dietary_fiber_g, tv_dish_detail_carbohydrate_g, tv_dish_detail_fat_g, tv_dish_detail_ingredient, tv_dish_detail_notes, tv_dish_detail_protein_g, tv_dish_detail_price;
    private LinearLayout ll_dish_detail;
    private ProgressBar pb_dish_detail_load;

    private Dish dish;
    private final int LOAD_DISH_DETAIL_SUCCESSFULLY = 101, LOAD_DISH_DETAIL_UNSUCCESSFULLY = 102, LOAD_DISH_DETAIL_NULL_DATA = 103;
    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_DISH_DETAIL_SUCCESSFULLY:
                    /* 给用户反馈，通知加载成功 */
                    pb_dish_detail_load.setVisibility(View.GONE);
                    iv_dish_detail_load.setVisibility(View.GONE);
                    ll_dish_detail.setVisibility(View.VISIBLE);
                    /* 设置组件的内容 */
                    tv_dish_detail_name.setText(dish.getName());
                    iv_dish_detail_picture.setImageBitmap(dish.getPicture());
                    tv_dish_detail_calorie_k_calorie.setText(("卡路里：" + dish.getCalorie_k_calorie() + "千卡"));
                    tv_dish_detail_carbohydrate_g.setText(("碳水化合物：" + dish.getCarbohydrate_g() + "克"));
                    tv_dish_detail_fat_g.setText(("脂肪：" + dish.getFat_g() + "克"));
                    tv_dish_detail_protein_g.setText(("蛋白质：" + dish.getProtein_g() + "克"));
                    tv_dish_detail_dietary_fiber_g.setText(("膳食纤维：" + dish.getDietary_fiber_g() + "克"));
                    tv_dish_detail_ingredient.setText(("主要成分：" + dish.getIngredient()));
                    tv_dish_detail_category.setText(("类别：" + dish.getCategory()));
                    tv_dish_detail_price.setText(("价格：" + dish.getPrice() + "元"));
                    tv_dish_detail_notes.setText((dish.getNotes()));
                    break;
                case LOAD_DISH_DETAIL_UNSUCCESSFULLY:
                    /* 给用户反馈，通知加载失败 */
                    pb_dish_detail_load.setVisibility(View.GONE);
                    iv_dish_detail_load.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.load_failed));
                    iv_dish_detail_load.setVisibility(View.VISIBLE);
                    tv_dish_detail_name.setText("加载失败");
                    break;
                case LOAD_DISH_DETAIL_NULL_DATA:
                    /* 给用户反馈，通知空空如也 */
                    pb_dish_detail_load.setVisibility(View.GONE);
                    iv_dish_detail_load.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_more));
                    iv_dish_detail_load.setVisibility(View.VISIBLE);
                    tv_dish_detail_name.setText("系统没有找到");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        tv_dish_detail_name = findViewById(R.id.tv_dish_detail_name);
        iv_dish_detail_picture = findViewById(R.id.iv_dish_detail_picture);
        tv_dish_detail_calorie_k_calorie = findViewById(R.id.tv_dish_detail_calorie_k_calorie);
        tv_dish_detail_category = findViewById(R.id.tv_dish_detail_category);
        tv_dish_detail_dietary_fiber_g = findViewById(R.id.tv_dish_detail_dietary_fiber_g);
        tv_dish_detail_carbohydrate_g = findViewById(R.id.tv_dish_detail_carbohydrate_g);
        tv_dish_detail_fat_g = findViewById(R.id.tv_dish_detail_fat_g);
        tv_dish_detail_ingredient = findViewById(R.id.tv_dish_detail_ingredient);
        tv_dish_detail_notes = findViewById(R.id.tv_dish_detail_notes);
        tv_dish_detail_protein_g = findViewById(R.id.tv_dish_detail_protein_g);
        tv_dish_detail_price = findViewById(R.id.tv_dish_detail_price);
        ll_dish_detail = findViewById(R.id.ll_dish_detail);
        pb_dish_detail_load = findViewById(R.id.pb_dish_detail_load);
        iv_dish_detail_load = findViewById(R.id.iv_dish_detail_load);
        /* 获取上个界面传来的值，默认值为101 */
        Intent intent = getIntent();
        final int id_of_dish_from_last_intent = intent.getIntExtra("id_of_dish", 101);
        /* 需要网络，在线程中进行 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                dish = null;
                DatabaseConnector databaseConnector = new DatabaseConnector(DishDetailActivity.this);
                databaseConnector.connectToDatabase();
                dish = databaseConnector.getDishByID(id_of_dish_from_last_intent);
                if (dish != null) {
                    Message message = new Message();
                    message.what = LOAD_DISH_DETAIL_SUCCESSFULLY;
                    handler.sendMessage(message);
                }
                else {
                    Message message = new Message();
                    NetworkChecker networkChecker = new NetworkChecker(DishDetailActivity.this);
                    if (networkChecker.isConnecting()) {
                        /* 内部数据库为空，属于数据库问题 */
                        message.what = LOAD_DISH_DETAIL_NULL_DATA;
                    }
                    else {
                        /* 断开网络，故加载失败 */
                        message.what = LOAD_DISH_DETAIL_UNSUCCESSFULLY;
                    }
                    handler.sendMessage(message);
                }
                databaseConnector.closeDatabase();
            }
        }).start();
        /* 设置返回键的功能 */
        ImageButton ib_dish_back = findViewById(R.id.ib_dish_detail_back);
        ib_dish_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 结束当前页面 */
                ActivityCollector.finishActivity(DishDetailActivity.this);
            }
        });
    }
}
