package de.eww.bibapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import de.eww.bibapp.fragments.AbstractContainerFragment;
import de.eww.bibapp.fragments.AccountFragment;
import de.eww.bibapp.fragments.SettingsFragment;
import de.eww.bibapp.fragments.info.InfoContainerFragment;
import de.eww.bibapp.fragments.search.SearchContainerFragment;
import de.eww.bibapp.fragments.watchlist.WatchlistContainerFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Main Activity class, providing a first level tab host
 */
public class MainActivity extends FragmentActivity
{
	CustomFragmentTabHost mainTabHost;
	
	public static String currentTabId = "search";
	
	public static boolean isPadVersion = false;
	public static MainActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		MainActivity.instance = this;
		
		this.setContentView(R.layout.activity_main);
		
		// use the layout to determe target platform
		if ( this.findViewById(R.id.main_large_indicator) != null )
		{
			MainActivity.isPadVersion = true;
		}
		
		// setup action bar for tabs
		ActionBar actionBar = this.getActionBar();
		
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(true);
		
		// setup tabs
	    this.mainTabHost = (CustomFragmentTabHost) this.findViewById(R.id.main_tabhost);
	    this.mainTabHost.setup(this, this.getSupportFragmentManager(), R.id.main_realtabcontent);
	    
	    Resources resources = this.getResources();
	    this.addTab(SearchContainerFragment.class, "search", resources.getText(R.string.actionbar_search), resources.getDrawable(R.drawable.menu_search));
	    this.addTab(AccountFragment.class, "account", resources.getText(R.string.actionbar_account), resources.getDrawable(R.drawable.menu_account));
	    this.addTab(WatchlistContainerFragment.class, "watchlist", resources.getText(R.string.actionbar_watchlist), resources.getDrawable(R.drawable.menu_watchlist));
	    this.addTab(InfoContainerFragment.class, "info", resources.getText(R.string.actionbar_info), resources.getDrawable(R.drawable.menu_info));
	    this.addTab(SettingsFragment.class, "settings", resources.getText(R.string.actionbar_settings), resources.getDrawable(R.drawable.menu_settings));
	    
	    this.mainTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
	    {
			@Override
			public void onTabChanged(String tabId)
			{
				AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
	        	if ( containerFragment != null )
	        	{
	        		//containerFragment.clearStack();
	        		//containerFragment.clearFragments();
	        		//MainActivity.instance.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	        	}
				
				MainActivity.currentTabId = tabId;
			}
		});
	}
	
	private void addTab(final Class<?> claz, String tag, CharSequence title, Drawable image)
	{
		View tabView = this.createTabView(this.mainTabHost.getContext(), title, image);
		
		TabSpec tabSpec = this.mainTabHost.newTabSpec(tag).setIndicator(tabView);
		this.mainTabHost.addTab(tabSpec, claz, null);
	}
	
	private View createTabView(final Context context, final CharSequence title, final Drawable image)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tab, null);
		
		TextView textView = (TextView) view.findViewById(R.id.tabText);
		textView.setText(title);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.tabImage);
		imageView.setImageDrawable(image);
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		MainActivity.currentTabId = "search";
	}
	
	public void onBackPressed()
	{
		AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
		if ( containerFragment.getStackSize() > 1)
		{
			containerFragment.up();
		}
		else
		{
			super.onBackPressed();
		}
	}
}