package com.xumengqi.lclab;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xumengqi
 * @date 2019/08/09
 */
public class BannerOneActivity extends BaseActivity {
    private WebView wvBannerOneMain;
    private Long exitTime = (long)0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_one);

        wvBannerOneMain = findViewById(R.id.wv_banner_one_main);
        wvBannerOneMain.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        wvBannerOneMain.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                /* 设置标题 */
                int maxLengthOfTitle = 15;
                boolean isLongerThanTheMaximumLength = title.length() > maxLengthOfTitle;
                TextView tvBannerOneLoading = findViewById(R.id.tv_banner_one_loading);
                tvBannerOneLoading.setText((title.substring(0, isLongerThanTheMaximumLength ? maxLengthOfTitle : title.length()) + (isLongerThanTheMaximumLength ? "..." : "")));
            }
        });
        /* 配置信息 */
        WebSettings webSettings = wvBannerOneMain.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        /* 加载网址 */
        String url = "http://www.guanjinco.com/buildersystem/";
        wvBannerOneMain.loadUrl(url);

        /* 设置返回键 */
        ImageButton ibBannerOneBack = findViewById(R.id.ib_banner_one_back);
        ibBannerOneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishActivity(BannerOneActivity.this);
            }
        });
        /* 设置刷新功能 */
        ImageButton ibBannerOneRefresh = findViewById(R.id.ib_banner_one_refresh);
        ibBannerOneRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wvBannerOneMain.reload();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (wvBannerOneMain.canGoBack()) {
            wvBannerOneMain.goBack();
        }
        else {
            int duration = 2000;
            if ((System.currentTimeMillis() - exitTime) > duration) {
                Toast.makeText(BannerOneActivity.this, "再按一次返回主页", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                ActivityCollector.finishActivity(BannerOneActivity.this);
            }
        }
    }
}
