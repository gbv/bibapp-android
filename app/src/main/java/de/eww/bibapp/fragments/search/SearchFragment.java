package de.eww.bibapp.fragments.search;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import de.eww.bibapp.CustomFragmentTabHost;
import de.eww.bibapp.MainActivity;
import de.eww.bibapp.R;
import de.eww.bibapp.SearchAdapterInterface;
import de.eww.bibapp.adapters.SearchAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.SearchEntry;
import de.eww.bibapp.fragments.AbstractContainerFragment;
import de.eww.bibapp.fragments.detail.DetailFragment;

public class SearchFragment extends AbstractContainerFragment implements
	SearchAdapterInterface
{
	private CustomFragmentTabHost mTabHost;
	private DetailFragment padDetailFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search_content, container, false);
		
		this.mTabHost = (CustomFragmentTabHost) view.findViewById(R.id.search_tabhost);
	    this.mTabHost.setup(getActivity(), this.getChildFragmentManager(), R.id.search_realtabcontent);
		
	    Resources resources = this.getResources();
	    String localTitle = resources.getString(R.string.search_local);
	    
	    // if our current local catalog contains a short title, we append it to the basic local tab title
	    SharedPreferences settings = this.getActivity().getPreferences(0);
		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
		if (Constants.LOCAL_CATALOGS[spinnerValue].length > 2) {
			localTitle += " " + Constants.LOCAL_CATALOGS[spinnerValue][2];
		}
	    
	    this.addTab(LocalSearchFragment.class, "local", localTitle);
	    this.addTab(GVKSearchFragment.class, "gvk", resources.getString(R.string.search_gvk));
	    
	    if ( MainActivity.isPadVersion )
	    {
	    	FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
	    	if ( this.padDetailFragment == null )
	    	{
	    		this.padDetailFragment = (DetailFragment) Fragment.instantiate(this.getActivity(), DetailFragment.class.getName());
	    		transaction.add(R.id.large_search_detail, this.padDetailFragment, "large_detail");
	    	}
	    	else
	    	{
	    		transaction.remove(this.padDetailFragment);
	    		this.padDetailFragment = (DetailFragment) Fragment.instantiate(this.getActivity(), DetailFragment.class.getName());
	    		transaction.add(R.id.large_search_detail, this.padDetailFragment, "large_detail");
	    	}
	    	
	    	transaction.commit();
	    }
	    
	    // set title
  		ActionBar actionBar = this.getActivity().getActionBar();
  		actionBar.setTitle(R.string.actionbar_search);
  		actionBar.setSubtitle(null);
		actionBar.setDisplayHomeAsUpEnabled(false);
		
		return view;
	}
	
	private void addTab(final Class<?> claz, String tag, CharSequence title)
	{
		View tabView = this.createTabView(this.mTabHost.getContext(), title);
		
		TabSpec tabSpec = this.mTabHost.newTabSpec(tag).setIndicator(tabView);
		this.mTabHost.addTab(tabSpec, claz, null);
	}
	
	private View createTabView(final Context context, final CharSequence title)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tab_secondary, null);
		
		TextView textView = (TextView) view.findViewById(R.id.tabText);
		textView.setText(title);
		
		return view;
	}

	@Override
	public SearchAdapter getSearchAdapter()
	{
		return ((SearchAdapterInterface) this.mTabHost.getLastTab().getFragment()).getSearchAdapter();
	}
	
	@Override
	public LoaderManager getLoaderManager()
	{
		return ((SearchAdapterInterface) this.mTabHost.getLastTab().getFragment()).getLoaderManager();
	}
	
	public CustomFragmentTabHost getTabHost()
	{
		return this.mTabHost;
	}
	
	@Override
	public int getHits()
	{
		return ((SearchAdapterInterface) this.mTabHost.getLastTab().getFragment()).getHits();
	}
	
	@Override
	public ArrayList<SearchEntry> getResults()
	{
		return ((SearchAdapterInterface) this.mTabHost.getLastTab().getFragment()).getResults();
	}
}