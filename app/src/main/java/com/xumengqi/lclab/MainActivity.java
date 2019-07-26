package com.xumengqi.lclab;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private List<Fragment> fragmentList;
    private int lastShowFragment;

    /** 其代表了下标，不能轻易更改 */
    private final int ONE_FRAGMENT = 0, TWO_FRAGMENT = 1, THREE_FRAGMENT = 2, FOUR_FRAGMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 初始化所有帧，并设置首个帧 */
        initializeFragments();
        /* 将初始化后的帧放到容器里，并显示首个帧 */
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_main_container, fragmentList.get(lastShowFragment));
        fragmentTransaction.show(fragmentList.get(lastShowFragment)).commitAllowingStateLoss();
        /* 设置组件底部导航栏 */
        BottomNavigationView bnv_main = findViewById(R.id.bnv_main);
        bnv_main.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            /* 当导航栏元素被点击时，切换当前帧 */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_one:
                        if (!(lastShowFragment == ONE_FRAGMENT)) {
                            changeShowFragment(lastShowFragment, ONE_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_two:
                        if (!(lastShowFragment == TWO_FRAGMENT)) {
                            changeShowFragment(lastShowFragment, TWO_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_three:
                        if (!(lastShowFragment == THREE_FRAGMENT)) {
                            changeShowFragment(lastShowFragment, THREE_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_four:
                        if (!(lastShowFragment == FOUR_FRAGMENT)) {
                            changeShowFragment(lastShowFragment, FOUR_FRAGMENT);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    /** 初始化所有帧，并设置首个帧 */
    public void initializeFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ContentFragment());
        fragmentList.add(new CommunityFragment());
        fragmentList.add(new StoreFragment());
        fragmentList.add(new MineFragment());
        lastShowFragment = ONE_FRAGMENT;
    }

    /** 隐藏当前帧，添加并显示下一帧 */
    public void changeShowFragment(int mLastShowFragment, int willShowFragment) {
        /* 隐藏当前帧 */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(fragmentList.get(mLastShowFragment));
        /* 添加并显示下一帧 */
        if (!fragmentList.get(willShowFragment).isAdded()) {
            fragmentTransaction.add(R.id.fl_main_container, fragmentList.get(willShowFragment));
        }
        fragmentTransaction.show(fragmentList.get(willShowFragment)).commitAllowingStateLoss();
        /* 更新状态 */
        lastShowFragment = willShowFragment;
    }
}
