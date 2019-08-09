package com.xumengqi.lclab;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class MainActivity extends BaseActivity {
    private List<Fragment> fragmentList;
    private int lastShowFragment;

    /** 其代表了下标，不能轻易更改 */
    private final int ONE_FRAGMENT = 0, TWO_FRAGMENT = 1, THREE_FRAGMENT = 2, FOUR_FRAGMENT = 3;
    private Long exitTime = (long)0;
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
        /* 加载方式：从第四帧->第三帧->第二帧->第一帧 */
        /* 而不能使用：第一帧->第二帧->第三帧->第四帧->第一帧，相当于快速双击第一帧 */
        changeShowFragment(lastShowFragment, THREE_FRAGMENT);
        changeShowFragment(lastShowFragment, TWO_FRAGMENT);
        changeShowFragment(lastShowFragment, ONE_FRAGMENT);

        /* 设置组件底部导航栏 */
        BottomNavigationView bnvMain = findViewById(R.id.bnv_main);
        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            /* 当导航栏元素被点击时，切换当前帧 */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_one:
                        if (lastShowFragment != ONE_FRAGMENT) {
                            changeShowFragment(lastShowFragment, ONE_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_two:
                        if (lastShowFragment != TWO_FRAGMENT) {
                            changeShowFragment(lastShowFragment, TWO_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_three:
                        if (lastShowFragment != THREE_FRAGMENT) {
                            changeShowFragment(lastShowFragment, THREE_FRAGMENT);
                        }
                        return true;
                    case R.id.navigation_item_four:
                        if (lastShowFragment != FOUR_FRAGMENT) {
                            changeShowFragment(lastShowFragment, FOUR_FRAGMENT);
                        }
                        return true;
                        default:
                }
                return false;
            }
        });
    }

    /** 初始化所有帧，并设置首个帧 */
    public void initializeFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ContentFragment());
        fragmentList.add(new RecordFragment());
        fragmentList.add(new StoreFragment());
        fragmentList.add(new MineFragment());
        lastShowFragment = FOUR_FRAGMENT;
    }

    /** 隐藏当前帧，添加并显示下一帧 */
    public void changeShowFragment(int mLastShowFragment, int willShowFragment) {
        /* 隐藏当前帧 */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(fragmentList.get(mLastShowFragment));
        /* 添加并显示下一帧 */
        if (!fragmentList.get(willShowFragment).isAdded()) {
            /* 快速双击帧时，isAdd()方法并不能判断帧是否添加，会导致Fragment already added */
            /* 如何避免：1.避免按钮同帧双击（物理）；2.避免代码加载双帧（代码） */
            fragmentTransaction.add(R.id.fl_main_container, fragmentList.get(willShowFragment));
        }
        fragmentTransaction.show(fragmentList.get(willShowFragment)).commitAllowingStateLoss();
        /* 更新状态 */
        lastShowFragment = willShowFragment;
    }

    @Override
    public void onBackPressed() {
        int duration = 2000;
        if (System.currentTimeMillis() - exitTime < duration) {
            ActivityCollector.finishAll();
        }
        else {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }
    }
}
