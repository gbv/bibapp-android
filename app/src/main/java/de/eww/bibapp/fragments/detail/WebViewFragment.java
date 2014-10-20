package de.eww.bibapp.fragments.detail;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.Map;

import de.eww.bibapp.MainActivity;
import de.eww.bibapp.R;
import de.eww.bibapp.WebURLProvider;
import de.eww.bibapp.fragments.AbstractContainerFragment;
import de.eww.bibapp.tasks.HeaderRequest;

public class WebViewFragment extends DialogFragment
{
	private String url;
    private WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// enable option menu
		this.setHasOptionsMenu(true);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		WebURLProvider urlProvider = (WebURLProvider) DetailFragment.current;
		this.url = urlProvider.getWebURL();
		
		ActionBar actionBar = MainActivity.instance.getActionBar();
		
		// set title
		if ( MainActivity.currentTabId.equals("search") )
		{
			actionBar.setTitle(R.string.actionbar_search);
		}
		else
		{
			actionBar.setTitle(R.string.actionbar_watchlist);
		}
		
		actionBar.setSubtitle(null);
		
		// enable up navigation
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_web_view, container, false);
		
		this.webView = (WebView) v.findViewById(R.id.web_view);
		this.webView.getSettings().setJavaScriptEnabled(true);
		
		if ( urlProvider.showWebExtern() == false )
		{
			this.webView.setWebViewClient(new WebViewClient());
		}

        // try to detect header information
        new HeaderRequest(this).execute(this.url);
		
		return v; 
	}

    public void onHeaderRequestDone(Map<String, List<String>> header) {
        // check if we got a pdf file
        boolean isPDF = false;
        List<String> types;

        if (header.containsKey("Content-Type")) {
            types = header.get("Content-Type");

            if (!types.isEmpty()) {
                String contentType = types.get(0);

                if (contentType.contains("pdf")) {
                    isPDF = true;
                }
            }
        }

        if (isPDF) {
            this.url = "https://docs.google.com/viewer?url=" + this.url;
        }

        this.webView.loadUrl(this.url);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch ( item.getItemId() )
	    {
	        case android.R.id.home:
	        	// app icon in action bar clicked; go up
	        	AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
	        	containerFragment.up();
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}