package com.yu.mae.bundles.scanner.demo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;

/**
 * Created by liyu20 on 2017/10/27.
 */

public class WebViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent((ViewGroup) findViewById(R.id.layout), new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .createAgentWeb()//
                .ready()
                .go(getIntent().getStringExtra("url"));

        /*WebView webView = findViewById(R.id.webview);
        setListener(webView);
        webView.loadUrl(getIntent().getStringExtra("url"));

        final ProgressBar bar = findViewById(R.id.progress);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });*/
    }

    private void setListener(final WebView webView) {
        // 增加js支持
        webView.getSettings().setJavaScriptEnabled(true);
        // 视频播放有声音无图像问题
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // 自动加载图片
        webView.getSettings().setLoadsImagesAutomatically(true);
        // 控件滚动条位置
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // 支持JavaScript调用
        webView.requestFocus();


        // 与网页配合，支持手势  <meta name="viewport" content="width=device-width,user-scalable=yes  initial-scale=1.0, maximum-scale=4.0">
        WebSettings settings = webView.getSettings();
        webView.setVerticalScrollbarOverlay(true);
        /**
         * viewPort这个变量如果设置ture，会造成部分5.0手机浏览部分H5页面放大到最大倍数
         * zhangjie78
         */
        //settings.setUseWideViewPort(false);//设定支持viewport
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);//设定支持缩放

        /***打开本地缓存提供JS调用**/
        webView.getSettings().setDomStorageEnabled(true);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getExternalCacheDir().getAbsolutePath();//Apps.getAppContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        //启用地理定位
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        settings.setGeolocationDatabasePath(appCachePath);

        // 允许 https 加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /*// 点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {    // 表示按返回键时的操作
                            webView.goBack();
                            return true; // 已处理
                        }
                    }
                    return false;
                }
            }
        });*/
    }
}
