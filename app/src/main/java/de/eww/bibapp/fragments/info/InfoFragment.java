//package de.eww.bibapp.fragments.info;
//
//import android.app.ActionBar;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import java.util.List;
//
//import de.eww.bibapp.AsyncCanceledInterface;
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.adapters.RSSAdapter;
//import de.eww.bibapp.constants.Constants;
//import de.eww.bibapp.data.NewsEntry;
//import de.eww.bibapp.fragments.AbstractListFragment;
//import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
//import de.eww.bibapp.tasks.NewsRSSLoader;
//
///**
//* @author Christoph Schönfeld - effective WEBWORK GmbH
//*
//* This file is part of the Android BibApp Project
//* =========================================================
//* Info Fragment class, providing a container for informative content and news feeds
//*/
//public class InfoFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<List<NewsEntry>>,
//	AsyncCanceledInterface
//{
//	// This is the Adapter being used to display the list's data.
//    RSSAdapter mAdapter;
//
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState)
//	{
//        super.onActivityCreated(savedInstanceState);
//
//        // Check if a rss feed is given
//        if ( !Constants.NEWS_URL.isEmpty() )
//        {
//        	// Force recreation of loader
//            this.getLoaderManager().destroyLoader(0);
//            getLoaderManager().initLoader(0, null, this);
//
//            this.mAdapter = new RSSAdapter(getActivity(), R.layout.rss_item_view);
//
//            this.setListAdapter(mAdapter);
//            this.isListShown = true;
//            this.setListShown(false);
//        }
//
//        ActionBar actionBar = MainActivity.instance.getActionBar();
//
//        // set title
//		actionBar.setTitle(R.string.actionbar_info);
//		actionBar.setSubtitle(null);
//
//		// disable up navigation
//		actionBar.setDisplayHomeAsUpEnabled(false);
//
//		this.getActivity().invalidateOptionsMenu();
//    }
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_info, container, false);
//
//        // check if a homepage url is given
//        SharedPreferences settings = this.getActivity().getPreferences(0);
//        int localCatalog = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
//
//        if (Constants.HOMEPAGE_URLS.length >= localCatalog+1) {
//            Button homepageButton = (Button) v.findViewById(R.id.info_button_homepage);
//            homepageButton.setVisibility(View.VISIBLE);
//            homepageButton.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                InfoFragment.this.onClickHomepageButton(v);
//                }
//            });
//        }
//
//		return v;
//	}
//
//	private void onClickContactButton(View v)
//	{
//		this.getLoaderManager().destroyLoader(0);
//
//		InfoContainerFragment infoFragment = (InfoContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("info");
//		infoFragment.switchContent(R.id.info_container, ContactFragment.class.getName(), "info_contact", true);
//	}
//
//	private void onClickLocationsButton(View v)
//	{
//		this.getLoaderManager().destroyLoader(0);
//
//		InfoContainerFragment infoFragment = (InfoContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("info");
//		infoFragment.switchContent(R.id.info_container, LocationsFragment.class.getName(), "info_locations", true);
//	}
//
//	private void onClickImpressumButton(View v)
//	{
//		this.getLoaderManager().destroyLoader(0);
//
//		InfoContainerFragment infoFragment = (InfoContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("info");
//		infoFragment.switchContent(R.id.info_container, ImpressumFragment.class.getName(), "info_impressum", true);
//	}
//
//    private void onClickHomepageButton(View v)
//    {
//        this.getLoaderManager().destroyLoader(0);
//
//        SharedPreferences settings = this.getActivity().getPreferences(0);
//        int localCatalog = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
//
//
//        Uri homepageUrl = Uri.parse(Constants.HOMEPAGE_URLS[localCatalog]);
//        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, homepageUrl);
//        this.startActivity(launchBrowser);
//    }
//
//	@Override
//	public Loader<List<NewsEntry>> onCreateLoader(int arg0, Bundle arg1)
//	{
//		return new NewsRSSLoader(getActivity().getApplicationContext(), this);
//	}
//
//	@Override
//	public void onLoadFinished(Loader<List<NewsEntry>> loader, List<NewsEntry> data)
//	{
//		this.mAdapter.clear();
//		this.mAdapter.addAll(data);
//		this.setListShown(true);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<List<NewsEntry>> arg0)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onAsyncCanceled()
//	{
//		this.setListShown(true);
//
//		if ( this.getView() != null )
//		{
//			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
//			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
//		}
//	}
//}