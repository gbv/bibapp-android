package de.eww.bibapp.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.Map;

import de.eww.bibapp.R;
import de.eww.bibapp.tasks.HeaderRequest;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 10.11.14.
 */
@ContentView(R.layout.activity_web)
public class WebViewActivity extends RoboActivity {

    @InjectView(R.id.web) WebView mWebView;

    String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getIntent().getExtras().getString("url");

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
            mUrl = "https://docs.google.com/viewer?url=" + mUrl;
        }

        mWebView.loadUrl(mUrl);
    }
}