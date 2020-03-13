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

    /** 代表所有帧的下标，不能修改 */
    private final int ONE_FRAGMENT = 0, TWO_FRAGMENT = 1, THREE_FRAGMENT = 2, FOUR_FRAGMENT = 3;
    /** 用于记录按下返回键的时刻 */
    private Long exitTime = (long) 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 初始化所有帧，并标注哪一个是第一帧 */
        initializeFragments();

        /* 显示方才标注的第一帧 */
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_main_container, fragmentList.get(lastShowFragment));
        fragmentTransaction.show(fragmentList.get(lastShowFragment)).commitAllowingStateLoss();

        /* 正确加载方式： 第四帧->第三帧->第二帧->第一帧 */
        /* 错误加载方式：第一帧->第二帧->第三帧->第四帧->第一帧 */
        /* 错误原因：见changeShowFragment()方法中的注解 */
        changeShowFragment(lastShowFragment, THREE_FRAGMENT);
        changeShowFragment(lastShowFragment, TWO_FRAGMENT);
        changeShowFragment(lastShowFragment, ONE_FRAGMENT);

        /* 当导航栏元素被点击时，切换当前帧 */
        BottomNavigationView bnvMain = findViewById(R.id.bnv_main);
        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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

    /**
     * 初始化帧列表，并标注哪一个是第一帧
     */
    public void initializeFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ContentFragment());
        fragmentList.add(new RecordFragment());
        fragmentList.add(new StoreFragment());
        fragmentList.add(new MineFragment());
        lastShowFragment = FOUR_FRAGMENT;
    }

    /**
     * 隐藏当前帧，（添加）并显示下一帧
     * @param mLastShowFragment 当前显示的帧
     * @param willShowFragment 接下来要显示的帧
     */
    public void changeShowFragment(int mLastShowFragment, int willShowFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(fragmentList.get(mLastShowFragment));

        /* 如果下一帧是没有被添加的，则添加 */
        /* 缺陷：当你以极快的速度添加同一帧时，isAdd()方法失效，进而导致“帧已经被添加”错误 */
        /* 避免：避免快速点击同一帧（物理方法），避免代码快速加载同一帧（代码方面），例如使用（第一帧->第二帧->第一帧）会报错 */
        if (!fragmentList.get(willShowFragment).isAdded()) {
            fragmentTransaction.add(R.id.fl_main_container, fragmentList.get(willShowFragment));
        }
        fragmentTransaction.show(fragmentList.get(willShowFragment)).commitAllowingStateLoss();

        /* 更新状态 */
        lastShowFragment = willShowFragment;
    }

    @Override
    public void onBackPressed() {
        /* 在一定的时间间隔里，双击了返回键则会退出应用 */
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