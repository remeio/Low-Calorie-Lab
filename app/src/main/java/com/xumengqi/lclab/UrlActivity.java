package com.xumengqi.lclab;

import android.annotation.SuppressLint;
import android.content.Intent;
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
public class UrlActivity extends BaseActivity {
    private WebView wvUrlMain;
    TextView tvBannerOneLoading;
    private Long exitTime = (long)0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        tvBannerOneLoading = findViewById(R.id.tv_url_title);
        /* 获取上个页面的传来的网址 */
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        /* 设置内置浏览器的相关配置 */
        wvUrlMain = findViewById(R.id.wv_url_main);
        wvUrlMain.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        wvUrlMain.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                /* 设置标题 */
                tvBannerOneLoading.setText(title);
            }
        });
        WebSettings webSettings = wvUrlMain.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        /* 设置屏幕适应 */
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        /* 加载网址 */
        wvUrlMain.loadUrl(url);

        /* 设置标题栏返回键 */
        ImageButton ibBannerOneBack = findViewById(R.id.ib_url_back);
        ibBannerOneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishActivity(UrlActivity.this);
            }
        });
        /* 设置标题栏刷新功能 */
        ImageButton ibBannerOneRefresh = findViewById(R.id.ib_url_refresh);
        ibBannerOneRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wvUrlMain.reload();
            }
        });
    }

    @Override
    public void onBackPressed() {
        /* 重写返回键为后退和返回 */
        if (wvUrlMain.canGoBack()) {
            wvUrlMain.goBack();
        }
        else {
            int duration = 2000;
            if ((System.currentTimeMillis() - exitTime) > duration) {
                Toast.makeText(UrlActivity.this, "再按一次返回主页", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                ActivityCollector.finishActivity(UrlActivity.this);
            }
        }
    }
}