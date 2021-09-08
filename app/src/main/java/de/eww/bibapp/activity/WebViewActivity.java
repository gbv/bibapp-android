package de.eww.bibapp.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

import de.eww.bibapp.R;
import de.eww.bibapp.tasks.HeaderRequest;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;

    String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mUrl = getIntent().getExtras().getString("url");

        mWebView = findViewById(R.id.web);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        // Try to detect header information
        new HeaderRequest(this).execute(mUrl);
    }

    public void onHeaderRequestDone(Map<String, List<String>> header) {
        // Check if we got a pdf file
        boolean isPdf = false;
        List<String> types;

        if (header.containsKey("Content-Type")) {
            types = header.get("Content-Type");

            if (!types.isEmpty()) {
                String contentType = types.get(0);

                if (contentType.contains("pdf")) {
                    isPdf = true;
                }
            }
        }

        if (isPdf) {
            mUrl = "http://docs.google.com/viewer?embedded=true&url=" + mUrl;
        }

        mWebView.loadUrl(mUrl);
    }
}