package com.xumengqi.lclab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class BaseActivity extends AppCompatActivity {
    /** 配合活动集中管理器使用：便于把握活动的生命周期 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ActivityCollector.removeActivity(this);
        Log.i(getClass().getSimpleName() + "_xmq", "onDestroy() is called!");
    }
}