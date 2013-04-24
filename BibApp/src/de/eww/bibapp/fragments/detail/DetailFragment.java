package de.eww.bibapp.fragments.detail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.DaiaLoaderInterface;
import de.eww.bibapp.MainActivity;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.UnApiLoaderInterface;
import de.eww.bibapp.WebURLProvider;
import de.eww.bibapp.adapters.AvailableAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.AvailableEntry;
import de.eww.bibapp.data.LocationsEntry;
import de.eww.bibapp.data.SearchEntry;
import de.eww.bibapp.fragments.AbstractContainerFragment;
import de.eww.bibapp.fragments.AbstractListFragment;
import de.eww.bibapp.fragments.dialogs.DetailActionsDialogFragment;
import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
import de.eww.bibapp.fragments.dialogs.PaiaActionDialogFragment;
import de.eww.bibapp.fragments.info.LocationsDetailFragment;
import de.eww.bibapp.tasks.DaiaLoaderCallback;
import de.eww.bibapp.tasks.DownloadImageTask;
import de.eww.bibapp.tasks.LocationsEntryTask;
import de.eww.bibapp.tasks.UnApiLoaderCallback;
import de.eww.bibapp.tasks.paia.PaiaRequestTask;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * ListFragment class, implementing the detail page
 */
public class DetailFragment extends AbstractListFragment implements
	DetailActionsDialogFragment.DetailActionsDialogLisener,
	DaiaLoaderInterface,
	PaiaHelper.PaiaListener,
	PaiaActionDialogFragment.PaiaActionDialogLisener,
	AsyncCanceledInterface,
	UnApiLoaderInterface,
	WebURLProvider
{
	// This is the Adapter being used to display the list's data.
    private AvailableAdapter mAdapter;
    
	private SearchEntry item = null;
	private PaiaActionDialogFragment paiaDialog;
	private LocationsEntry locationsEntry;
	private String webUrl;
	private boolean showWebExtern = false;
	private int listPosition;
	
	public static DetailFragment current;
	
	public static DetailFragment newInstance(SearchEntry item, int position)
	{
		DetailFragment fragment = new DetailFragment();
		fragment.item = item;
		fragment.listPosition = position;
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// enable option menu
		this.setHasOptionsMenu(true);
	}
	
	public LocationsEntry getItem()
	{
		return this.locationsEntry;
	}
	
	public SearchEntry getSearchItem()
	{
		return this.item;
	}
	
	public AvailableAdapter getAdapter()
	{
		return this.mAdapter;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
        
		if ( this.item != null )
		{
	        // Create an empty adapter we will use to display the loaded data.
			this.mAdapter = new AvailableAdapter(this.getActivity(), R.layout.fragment_detail_available_item_view);
	        this.setListAdapter(mAdapter);
	        
	        // Show progress indicators.
	        this.isListShown = true;
	        this.setListShown(false);
	        this.setUnApiShown(false);
	        
	        // Prepare the loaders. Either re-connect with an existing ones, or start a new ones.
	        LoaderManager loaderManager = this.getLoaderManager();
	        loaderManager.destroyLoader(0);
	        loaderManager.destroyLoader(1);
			loaderManager.initLoader(0, null, new DaiaLoaderCallback(this));
			loaderManager.initLoader(1, null, new UnApiLoaderCallback(this));
			
			// Set additional data
			this.mAdapter.setIsLocalSearch(this.item.isLocalSearch);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_detail_view, container, false);
		
		ActionBar actionBar = MainActivity.instance.getActionBar();
		
		// set title
		actionBar.setTitle(R.string.actionbar_search);
		actionBar.setSubtitle(null);
		
		/* fix for https://code.google.com/p/android/issues/detail?id=29472 */
		v.post(new Runnable() { public void run()
		{
			FragmentActivity main = DetailFragment.this.getActivity();
			
			if ( main != null )
			{
				DetailFragment.this.getActivity().invalidateOptionsMenu();
			}
		}
		});
		
		if ( this.item != null )
		{
			// set data
			TextView titleView = (TextView) v.findViewById(R.id.detail_item_title);
			TextView subView = (TextView) v.findViewById(R.id.detail_item_sub);
			
			titleView.setText(this.item.title);
			
			String subTitle = this.item.subTitle;
			if ( !this.item.partName.isEmpty() && !this.item.partNumber.isEmpty() && !subTitle.isEmpty() )
			{
				subTitle += "\n";
			}
			if ( !this.item.partName.isEmpty() )
			{
				subTitle += this.item.partName;
			}
			if ( !this.item.partNumber.isEmpty() )
			{
				subTitle += "; " + this.item.partNumber;
			}
			subView.setText(subTitle);
			
			// load image
			ImageView imageView = (ImageView) v.findViewById(R.id.detail_item_image);
			AsyncTask<String, Void, Bitmap> imageTask = new DownloadImageTask(imageView, this.item, this.getActivity());
			imageTask.execute(Constants.getImageUrl(this.item.isbn));
			
			// index
			LinearLayout indexLayoutView = (LinearLayout) v.findViewById(R.id.detail_index_container);
			if ( !this.item.indexArray.isEmpty() )
			{
				// show index container layout
				indexLayoutView.setVisibility(View.VISIBLE);
				
				// set click handling
				indexLayoutView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						DetailFragment.this.onClickIndex();
					}
				});
			}
			
			// interlanding
			if ( this.item.isLocalSearch == false )
			{
				LinearLayout interlandingLayoutView = (LinearLayout) v.findViewById(R.id.detail_interlanding_container);
				
				// show
				interlandingLayoutView.setVisibility(View.VISIBLE);
				
				// set click handling
				interlandingLayoutView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v) {
						DetailFragment.this.onClickInterlanding();
					}
				});
			}
		}
		
		return v; 
	}
	
	public void setSearchEntry(SearchEntry entry)
	{
		this.item = entry;
		View v = this.getView();
		
		this.getActivity().invalidateOptionsMenu();
		
		RelativeLayout placeholderView = (RelativeLayout) v.findViewById(R.id.detail_placeholder);
		if ( placeholderView != null )
		{
			// this is the pad layout
			LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.detail_content);
			
			// if this is the first call, hide the placeholder and show the detail view
			if ( placeholderView.isShown() )
			{
				placeholderView.setVisibility(View.GONE);
				linearLayout.setVisibility(View.VISIBLE);
			}
		}
		
		// set data
		TextView titleView = (TextView) v.findViewById(R.id.detail_item_title);
		TextView subView = (TextView) v.findViewById(R.id.detail_item_sub);
		
		titleView.setText(this.item.title);
		
		String subTitle = this.item.subTitle;
		if ( !this.item.partName.isEmpty() && !this.item.partNumber.isEmpty() && !subTitle.isEmpty() )
		{
			subTitle += "\n";
		}
		if ( !this.item.partName.isEmpty() )
		{
			subTitle += this.item.partName;
		}
		if ( !this.item.partNumber.isEmpty() )
		{
			subTitle += "; " + this.item.partNumber;
		}
		subView.setText(subTitle);
		
		// load image
		ImageView imageView = (ImageView) v.findViewById(R.id.detail_item_image);
		AsyncTask<String, Void, Bitmap> imageTask = new DownloadImageTask(imageView, this.item, this.getActivity());
		imageTask.execute(Constants.getImageUrl(this.item.ppn));
		
		// index
		LinearLayout indexLayoutView = (LinearLayout) v.findViewById(R.id.detail_index_container);
		if ( !this.item.indexArray.isEmpty() )
		{
			// show index container layout
			indexLayoutView.setVisibility(View.VISIBLE);
			
			// set click handling
			indexLayoutView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					DetailFragment.this.onClickIndex();
				}
			});
		}
		
		// interlanding
		if ( this.item.isLocalSearch == false )
		{
			LinearLayout interlandingLayoutView = (LinearLayout) v.findViewById(R.id.detail_interlanding_container);
			
			// show
			interlandingLayoutView.setVisibility(View.VISIBLE);
			
			// set click handling
			interlandingLayoutView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					DetailFragment.this.onClickInterlanding();
				}
			});
		}
		
		// Create an empty adapter we will use to display the loaded data.
		this.mAdapter = new AvailableAdapter(this.getActivity(), R.layout.fragment_detail_available_item_view);
        this.setListAdapter(mAdapter);
        
        // Show progress indicators.
        this.isListShown = true;
        this.setListShown(false);
        this.setUnApiShown(false);
        
        // Prepare the loaders. Either re-connect with an existing ones, or start a new ones.
        LoaderManager loaderManager = this.getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.destroyLoader(1);
		loaderManager.initLoader(0, null, new DaiaLoaderCallback(this));
		loaderManager.initLoader(1, null, new UnApiLoaderCallback(this));
		
		// Set additional data
		this.mAdapter.setIsLocalSearch(this.item.isLocalSearch);
	}
	
	private void setUnApiShown(boolean shown)
	{
		View progressBar = this.getView().findViewById(R.id.progress_bar_small);
		
		if ( shown == true )
		{
	        progressBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
			progressBar.setVisibility(View.GONE);
		}
		else
		{
	        progressBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	public String getWebURL()
	{
		return this.webUrl;
	}
	
	private String getIndexUrl()
	{
		String indexUrl = "";
		
		// determ what actions are available for this location
		if ( this.item.onlineUrl.isEmpty() )
		{
			// determ the index to display
			Iterator<String> it = this.item.indexArray.iterator();
			
			while ( it.hasNext() )
			{
				// try to find pdf version
				indexUrl = it.next();
				
				if ( indexUrl.substring(indexUrl.length()-3, indexUrl.length()).equals("pdf") )
				{
					break;
				}
			}
		}
		
		return indexUrl;
	}
	
	private void onClickInterlanding()
	{
		Uri uriUrl = Uri.parse(Constants.getInterlendingUrl(this.item.ppn));
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		this.startActivity(launchBrowser);
	}
	
	private void onClickIndex()
	{
		this.webUrl = this.getIndexUrl();
		this.showWebExtern = false;
		DetailFragment.current = this;
		
		AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
		
		int containerId;
		
		if ( MainActivity.currentTabId.equals("search") )
		{
			containerId = R.id.search_container;
		}
		else
		{
			containerId = R.id.watchlist_container;
		}
		
		if ( !MainActivity.isPadVersion )
		{
			containerFragment.switchContent(containerId, WebViewFragment.class.getName(), "detail_index", true);
		}
		else
		{
			containerFragment.switchContent(containerId, WebViewFragment.class.getName(), "detail_index", true);
			/*WebViewFragment webViewFragment = new WebViewFragment();
			webViewFragment.show(this.getActivity().getSupportFragmentManager(), "web_view_dialog");*/
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		this.lastClickedPosition = position;
		
		// determ what actions are available for this location
		ArrayList<String> actionList = new ArrayList<String>();
		if ( this.item.onlineUrl.isEmpty() )
		{
			// this is not an online resource
			AvailableEntry availableEntry = this.mAdapter.getItem(position);
			String actions = availableEntry.actions;
			
			// location | request | order
			if ( actions.contains("location") )
			{
				actionList.add("location");
			}
			if ( actions.contains("request") )
			{
				actionList.add("request");
			}
			if ( actions.contains("order") )
			{
				actionList.add("order");
			}
		}
		else
		{
			AvailableEntry availableEntry = this.mAdapter.getItem(position);
			String actions = availableEntry.actions;
			
			// location
			if ( actions.contains("location") )
			{
				actionList.add("location");
			}
			actionList.add("online");
		}
		
		// open a dialog with all available actions
		DetailActionsDialogFragment dialogFragment = new DetailActionsDialogFragment();
		dialogFragment.setActionList(actionList);
		dialogFragment.show(this.getChildFragmentManager(), "detail_actions");
	}
	
	public void onLocationLoadFinished(LocationsEntry entry)
	{
		LocationsDetailFragment.entry = entry;
		
		AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
		
		int containerId;
		
		if ( MainActivity.currentTabId.equals("search") )
		{
			containerId = R.id.search_container;
		}
		else
		{
			containerId = R.id.watchlist_container;
		}
		
		if ( !MainActivity.isPadVersion )
		{
			containerFragment.switchContent(containerId, LocationsDetailFragment.class.getName(), "detail_location", true);
		}
		else
		{
			containerFragment.switchContent(containerId, LocationsDetailFragment.class.getName(), "detail_location", true);
			/*LocationsDetailFragment locationsDetailFragment = new LocationsDetailFragment();
			locationsDetailFragment.show(this.getActivity().getSupportFragmentManager(), "locations_detail_dialog");*/
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch ( item.getItemId() )
	    {
	        /*case android.R.id.home:
	        	// app icon in action bar clicked; go up
	        	SearchContainerFragment searchContainer = (SearchContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("search");
	        	searchContainer.switchContent(SearchFragment.class.getName());
	    		
	            return true;*/
	        case R.id.menu_detail_add_to_watchlist:
	        	// add to watchlist clicked
	        	if ( this.item != null )
	        	{
	        		// get actual watchlist
		        	ArrayList<SearchEntry> watchlistEntries = new ArrayList<SearchEntry>();
		        	
		        	File file = this.getActivity().getFileStreamPath("watchlist");
		        	if ( file.isFile() )
		        	{
		        		try
		        		{
			    			FileInputStream fis = this.getActivity().openFileInput("watchlist");
			    			
			    			ObjectInputStream ois = new ObjectInputStream(fis);
			    			watchlistEntries = (ArrayList<SearchEntry>) ois.readObject();
			    			
			    			fis.close();
			    		}
			    		catch (Exception e)
			    		{
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
		        	}
		        	
		        	// add new entry and store the watchlist
		        	watchlistEntries.add(this.item);
					
					try
					{
						FileOutputStream fos = this.getActivity().openFileOutput("watchlist", Context.MODE_PRIVATE);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(watchlistEntries);
						oos.close();
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// display toast
					Context context = this.getActivity().getApplicationContext();
					Resources resource = this.getActivity().getResources();
					
					Toast toast = Toast.makeText(context, resource.getText(R.string.toast_watchlist_added), Toast.LENGTH_SHORT);
					toast.show();
					
					// disalbe menu item
					item.setEnabled(false);
	        	}
				
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onActionLocation(DialogFragment dialog)
	{
		AsyncTask<String, Void, LocationsEntry> locationsEntryTask = new LocationsEntryTask(this);
		locationsEntryTask.execute(this.mAdapter.getItem(this.lastClickedPosition).uriUrl);
	}

	@Override
	public void onActionRequest(DialogFragment dialog)
	{
		// ensure paia connection
		PaiaHelper paiaHelper = new PaiaHelper(this);
		paiaHelper.ensureConnection();
	}

	@Override
	public void onActionOrder(DialogFragment dialog)
	{
		// ensure paia connection
		PaiaHelper paiaHelper = new PaiaHelper(this);
		paiaHelper.ensureConnection();
	}
	
	@Override
	public void onActionOnline(DialogFragment dialog)
	{
		Uri uriUrl = Uri.parse(this.item.onlineUrl);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		this.startActivity(launchBrowser);
	}
	
	public void onRequest(JSONObject response)
	{
		// determ text to display
		String responseText = "";
		
		Resources resources = this.getActivity().getResources();
		
		try {
			if ( response.getJSONArray("array").length() == 0 )
			{
				responseText = (String) resources.getText(R.string.paiadialog_general_failure);
			}
			else
			{
				responseText = (String) resources.getText(R.string.paiadialog_general_success);
			}
		}
		catch (JSONException e)
		{
			responseText = (String) resources.getText(R.string.paiadialog_general_failure);
		}
		
		this.paiaDialog.paiaActionDone(responseText);
		
		// reload daia information
		this.mAdapter.clear();
		
		this.setListShown(false);
		
		this.getLoaderManager().getLoader(0).forceLoad();
	}

	@Override
	public void onPaiaConnected()
	{
		// start async task to send paia request
    	JSONArray jsonArray = new JSONArray();
    	JSONObject itemObject = new JSONObject();
    	
    	try
    	{
    		// get uri from daia and assemble request array
    		AvailableEntry daiaEntry = this.mAdapter.getItem(this.lastClickedPosition);
    		
			itemObject.put("item", daiaEntry.itemUriUrl);
			jsonArray.put(itemObject);
	    	
			AsyncTask<String, Void, JSONObject> requestTask = new PaiaRequestTask(this);
			requestTask.execute(jsonArray.toString());
		}
    	catch (JSONException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	// show the action dialog
    	this.paiaDialog = new PaiaActionDialogFragment();
		this.paiaDialog.show(this.getChildFragmentManager(), "paia_action");
	}

	@Override
	public void onActionDialogPositiveClick(DialogFragment dialog)
	{
		// close dialog
		this.paiaDialog.dismiss();
	}

	@Override
	public void onAsyncCanceled()
	{
		this.setListShown(true);
		
		if ( this.getView() != null )
		{
			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
		}
	}

	@Override
	public boolean showWebExtern() {
		return this.showWebExtern;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		
		if ( !MainActivity.currentTabId.equals("watchlist") && !MainActivity.isPadVersion )
		{
			menu.clear();
		}
		
		if ( this.item != null )
		{
			this.getActivity().getMenuInflater().inflate(R.menu.detail, menu);
			
			MenuItem menuItem = menu.findItem(R.id.menu_detail_add_to_watchlist);
			
			// disable "add to watchlist" if we are comming from watchlist fragment
			if ( MainActivity.currentTabId.equals("watchlist") )
			{
				menuItem.setEnabled(false);
				menuItem.setTitle(R.string.menu_detail_addtowatchlist_duplicate);
			}
			else
			{
				// disable "add to watchlist" if the item is already in watchlist and change text
				// get actual watchlist
	        	ArrayList<SearchEntry> watchlistEntries = new ArrayList<SearchEntry>();
	        	
	        	File file = this.getActivity().getFileStreamPath("watchlist");
	        	if ( file.isFile() )
	        	{
	        		try
	        		{
		    			FileInputStream fis = this.getActivity().openFileInput("watchlist");
		    			
		    			ObjectInputStream ois = new ObjectInputStream(fis);
		    			watchlistEntries = (ArrayList<SearchEntry>) ois.readObject();
		    			
		    			fis.close();
		    		}
		    		catch (Exception e)
		    		{
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
	        	}
	        	
	        	if ( watchlistEntries.contains(this.item) )
	        	{
	        		menuItem.setEnabled(false);
	        		menuItem.setTitle(R.string.menu_detail_addtowatchlist_duplicate);
	        	}
			}
		}
	}
}