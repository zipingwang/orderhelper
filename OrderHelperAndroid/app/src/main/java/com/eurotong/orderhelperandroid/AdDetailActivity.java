package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class AdDetailActivity extends Activity {

    Button btnGoBack;
    WebView webViewAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail);

        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        webViewAd=(WebView)findViewById(R.id.webViewAd);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();

        webViewAd.getSettings().setJavaScriptEnabled(true);
        webViewAd.loadUrl(Common.GetBaseUrl() + "AdContent.html");
    }
}
