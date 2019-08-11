package com.xumengqi.lclab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class ContentFragment extends Fragment {
    private List<FoodCategory> foodCategoryList;

    private ArrayList<Integer> bannerImagePath;
    private ArrayList<String> bannerImageTitle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 加载帧视图 */
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initializeBanner(view);

        RecyclerView rvContentFood = view.findViewById(R.id.rv_content_food);
        /* 初始化食材库分类列表 */
        initializeFoodCategory();
        /* 设置列表管理 */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 6);
        rvContentFood.setLayoutManager(gridLayoutManager);
        /* 设置适配器 */
        FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter(foodCategoryList);
        rvContentFood.setAdapter(foodCategoryAdapter);
        CardView cvContentSubOne = view.findViewById(R.id.cv_content_sub_one);
        cvContentSubOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("http://106.15.39.96/media/url/question/main.html");
            }
        });
        CardView cvContentSubTwo = view.findViewById(R.id.cv_content_sub_two);
        cvContentSubTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("http://www.leighpeele.com/mifflin-st-jeor-calculator");
            }
        });
        CardView cvContentSubThree = view.findViewById(R.id.cv_content_sub_three);
        cvContentSubThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("http://www.guanjinco.com/buildersystem/");
            }
        });
        return view;
    }

    public void initializeBanner(View view) {
        initializeBannerData();
        initializeBannerView(view);
    }

    private void initializeBannerData() {
        bannerImagePath = new ArrayList<>();
        bannerImageTitle = new ArrayList<>();
        bannerImagePath.add(R.drawable.banner1);
        bannerImagePath.add(R.drawable.banner2);
        bannerImagePath.add(R.drawable.banner3);
        bannerImageTitle.add("每日推荐");
        bannerImageTitle.add("低卡小贴士");
        bannerImageTitle.add("加盟");
    }

    private void initializeBannerView(final View view) {
        BannerImageLoader bannerImageLoader = new BannerImageLoader();
        Banner banner = view.findViewById(R.id.banner_content);
        /* 设置样式，里面有很多种样式可以自己都看看效果 */
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        /* 设置图片加载器 */
        banner.setImageLoader(bannerImageLoader);
        /* 设置轮播的动画效果,里面有很多种特效,可以都看看效果 */
        banner.setBannerAnimation(Transformer.Default);
        /* 轮播图片的文字 */
        banner.setBannerTitles(bannerImageTitle);
        /* 设置轮播间隔时间 */
        banner.setDelayTime(4000);
        /* 设置是否为自动轮播，默认是true */
        banner.isAutoPlay(true);
        /* 设置指示器的位置，小点点，居中显示 */
        banner.setIndicatorGravity(BannerConfig.CENTER);
        /* 设置图片加载地址 */
        banner.setImages(bannerImagePath);
        /* 轮播图的监听 */
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                switch (position) {
                    case 0:
                        openUrl("http://106.15.39.96/media/url/recommendation/main.html");
                        break;
                    case 1:
                        openUrl("http://106.15.39.96/media/url/tips/main.html");
                        break;
                    case 2:
                        openUrl("http://www.guanjinco.com/buildersystem/league.html");
                        break;
                        default:
                }
            }
        });
        /* 开始调用的方法，启动轮播图 */
        banner.start();
    }

    /** 图片加载类 */
    private class BannerImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
    }

    /** 初始化食材库分类列表 */
    private void initializeFoodCategory() {
        foodCategoryList = new ArrayList<>();
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.meat);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.milk);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.vegetable);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.staple_food);
        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.nuts);
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.drink);
        foodCategoryList.add(new FoodCategory(bitmap1, "肉类"));
        foodCategoryList.add(new FoodCategory(bitmap2, "奶类"));
        foodCategoryList.add(new FoodCategory(bitmap3, "蔬菜"));
        foodCategoryList.add(new FoodCategory(bitmap4, "主食"));
        foodCategoryList.add(new FoodCategory(bitmap5, "豆类"));
        foodCategoryList.add(new FoodCategory(bitmap6, "饮料"));
    }

    /** 打开网页界面 */
    public void openUrl(String url) {
        Intent intent = new Intent(this.getContext(), UrlActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}