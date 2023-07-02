package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PageLink extends AppCompatActivity {

    private Toolbar mtoolbar;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_link);

        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);

        getSupportActionBar().setTitle(getString(R.string.facepage));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        webView = findViewById(R.id.webView);
       // WebSettings webSettings = webView.getSettings();
       // webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.facebook.com/Masifkapp/");

        webView.getSettings().setJavaScriptEnabled(true);

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.setWebViewClient(new WebViewClient());


    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


}