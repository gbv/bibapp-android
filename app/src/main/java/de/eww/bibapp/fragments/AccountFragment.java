package de.eww.bibapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.CustomFragmentTabHost;
import de.eww.bibapp.MainActivity;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
import de.eww.bibapp.tasks.paia.PaiaPatronTask;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Account Fragment class, holds Paia list views
 */
public class AccountFragment extends AbstractContainerFragment implements
	PaiaHelper.PaiaListener,
	AsyncCanceledInterface
{
	private CustomFragmentTabHost mTabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.setHasOptionsMenu(true);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		PaiaHelper paiaHelper = new PaiaHelper(this);
	    paiaHelper.ensureConnection();
	    
	    this.getActivity().invalidateOptionsMenu();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_account_main, container, false);
		
		if ( !MainActivity.isPadVersion )
		{
			this.mTabHost = (CustomFragmentTabHost) v.findViewById(R.id.account_tabhost);
			this.mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.account_realtabcontent);
		    
		    this.mTabHost.addTab(	this.mTabHost.newTabSpec("dummy").setIndicator(""),
		    						DummyFragment.class, null);
		}
	    
	    // set title
 		ActionBar actionBar = this.getActivity().getActionBar();
 		actionBar.setTitle(R.string.actionbar_account);
 		actionBar.setDisplayHomeAsUpEnabled(false);
		
		return v;
	}
	
	public void onPatronLoaded(JSONObject response)
	{
		// set action bar sub title
		ActionBar actionBar = this.getActivity().getActionBar();
		
		try
		{
			String name = response.getString("name");
            actionBar.setSubtitle(name);

            if (response.has("status")) {
                int status = response.getInt("status");
                if (status > 0) {
                    String newTitle = actionBar.getTitle() + " " + this.getResources().getText(R.string.account_inactive);
                    actionBar.setTitle(newTitle);
                }
            }
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRealTabs()
	{
		this.mTabHost.clearAllTabs();
		
		Resources resources = this.getResources();
		
		this.addTab(AccountBorrowedFragment.class, "borrowed", resources.getString(R.string.account_borrowed));
		this.addTab(AccountBookedFragment.class, "booked", resources.getString(R.string.account_booked));
		this.addTab(AccountFeesFragment.class, "fees", resources.getString(R.string.account_fees));
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
	public void onPaiaConnected()
	{
		if ( !MainActivity.isPadVersion )
		{
			this.addRealTabs();
		}
		else
		{
			this.addPadFragments();
		}

		// perform a paia request to get the users name, if we have the scope to do this
        if (PaiaHelper.hasScope(PaiaHelper.SCOPES.READ_PATRON)) {
            AsyncTask<String, Void, JSONObject> paiaPatronTask = new PaiaPatronTask(this);
            paiaPatronTask.execute(PaiaHelper.getAccessToken(), PaiaHelper.getUsername());
        }
	}
	
	private void addPadFragments()
	{
		FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
		
		transaction.add(R.id.large_account_container, Fragment.instantiate(this.getActivity(), AccountBorrowedFragment.class.getName()));
		transaction.add(R.id.large_account_container, Fragment.instantiate(this.getActivity(), AccountBookedFragment.class.getName()));
		transaction.add(R.id.large_account_container, Fragment.instantiate(this.getActivity(), AccountFeesFragment.class.getName()));
		
		transaction.commit();
	}

	@Override
	public void onAsyncCanceled()
	{
		if ( this.getView() != null )
		{
			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menu.clear();
	}
}