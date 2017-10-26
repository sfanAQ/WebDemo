package com.sfan.webdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        switch (type) {
            case 0:// HTML
                String html = intent.getStringExtra("html");
                loadUrl(html);
                break;
            case 1:// 文本
                String text = intent.getStringExtra("text");
                loadData(text);
                break;
            case 2:// 接口
                String url = intent.getStringExtra("url");
                loadData(url);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void loadUrl(final String url) {
        // 防止跳出浏览器
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {// 监听进度
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                timer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        mWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                // 超时后,首先判断页面加载进度,超时并且进度小于100
                                if (mWebView.getProgress() < 100) {
                                    Toast.makeText(WebActivity.this, "当前网络加载很慢", Toast.LENGTH_LONG).show();
                                    if (timer != null) {
                                        timer.cancel();
                                        timer.purge();
                                    }
                                }
                            }
                        });
                    }
                };
                // 计时5s超时
                timer.schedule(tt, 5000, 1);
            }

            /**
             * onPageFinished指页面加载完成,完成后取消计时器
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                }
            }
        });
        mWebView.setFocusable(true);//获取焦点
        mWebView.requestFocus();
        WebSettings settings = mWebView.getSettings();
        settings.setAllowFileAccess(true);//允许访问文件数据
        settings.setDatabaseEnabled(true);//开启数据库

        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//设置缓冲的模式，本地缓存
        settings.setBlockNetworkImage(false);//显示网络图像
        settings.setLoadsImagesAutomatically(true);//显示网络图像
        settings.setPluginState(WebSettings.PluginState.ON);//插件支持
        settings.setSupportZoom(false);//设置是否支持变焦
        settings.setJavaScriptEnabled(true);//支持JavaScript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持JavaScriptEnabled

        settings.setGeolocationEnabled(true);//启用地理定位
        String dir = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();// 设置定位的数据库路径
        settings.setGeolocationDatabasePath(dir);//数据库
        settings.setDomStorageEnabled(true);//开启DomStorage缓存 （ 远程web数据的本地化存储）

//        settings.setUseWideViewPort(true);// 关键点
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 设置布局方式，自适应屏幕
//        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);// 启用localstorage本地存储api

        mWebView.post(new Runnable() {

            @Override
            public void run() {
                mWebView.loadUrl(url);
            }
        });
    }

    private void loadData(String text) {

    }

}
