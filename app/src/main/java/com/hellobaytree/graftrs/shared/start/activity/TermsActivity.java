package com.hellobaytree.graftrs.shared.start.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hellobaytree.graftrs.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gherg on 11/28/2016.
 */

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.web) WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.thesquareapp.com/terms");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
