package com.xumengqi.lclab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 当活动被创建时，执行列表里的添加 */
        ActivityCollector.addActivity(this);

        Log.i(getClass().getSimpleName() + "_xmq", "onCreate() is called!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(getClass().getSimpleName() + "_xmq", "onResume() is called!");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(getClass().getSimpleName() + "_xmq", "onPause() is called!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* 当活动被销毁时，执行列表里的移除 */
        ActivityCollector.removeActivity(this);

        Log.i(getClass().getSimpleName() + "_xmq", "onDestroy() is called!");
    }
}
