package de.eww.bibapp.fragments.detail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.eww.bibapp.MainActivity;
import de.eww.bibapp.R;
import de.eww.bibapp.WebURLProvider;
import de.eww.bibapp.fragments.AbstractContainerFragment;

public class WebViewFragment extends DialogFragment
{
	private String url;
	
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
		
		WebView webView = (WebView) v.findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		
		if ( urlProvider.showWebExtern() == false )
		{
			webView.setWebViewClient(new WebViewClient());
		}
		
		// if the url points to a pdf file, we will use google docs to display the content
		if ( this.url.length() >= 3 )
		{
			String fileType = this.url.substring(this.url.length()-3, this.url.length());
			Pattern pattern = Pattern.compile("pdf", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(fileType);
			if ( matcher.find() )
			{
				this.url = "https://docs.google.com/viewer?url=" + this.url;
			}
			webView.loadUrl(this.url);
		}
		
		return v; 
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