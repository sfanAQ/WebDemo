package com.sfan.webdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnText, R.id.btnRequest, R.id.btnHTML})
    public void onViewClicked(View view) {
        Intent html = new Intent(MainActivity.this, WebActivity.class);
        switch (view.getId()) {
            case R.id.btnHTML:
                html.putExtra("type", 0);
                html.putExtra("html", "http://leafletjs.com/examples/quick-start/example-basic.html");
                startActivity(html);
                break;
            case R.id.btnText:
                html.putExtra("type", 0);
                html.putExtra("html", "https://www.baidu.com");
                startActivity(html);
                break;
            case R.id.btnRequest:
                html.putExtra("type", 0);
                html.putExtra("html", "https://ditu.amap.com");
                startActivity(html);
                break;

        }
    }
}
