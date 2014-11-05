//package de.eww.bibapp.fragments.search;
//
//import android.app.ActionBar;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.res.Resources;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//
//import de.eww.bibapp.AsyncCanceledInterface;
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.SearchAdapterInterface;
//import de.eww.bibapp.adapters.SearchAdapter;
//import de.eww.bibapp.constants.Constants;
//import de.eww.bibapp.data.SearchEntry;
//import de.eww.bibapp.fragments.AbstractListFragment;
//import de.eww.bibapp.fragments.detail.DetailFragment;
//import de.eww.bibapp.fragments.detail.DetailPagerFragment;
//import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
//import de.eww.bibapp.tasks.DBSPixelTask;
//import de.eww.bibapp.tasks.SearchXmlLoader;
//
//public class LocalSearchFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
//	OnEditorActionListener,
//	OnScrollListener,
//	AsyncCanceledInterface,
//	SearchAdapterInterface
//{
//	// This is the Adapter being used to display the list's data.
//    SearchAdapter mAdapter;
//
//    private boolean isExpandLoading = false;
//    private int hits = 0;
//    private ArrayList<SearchEntry> previousResults = new ArrayList<SearchEntry>();
//
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id)
//	{
//		this.lastClickedPosition = position;
//		DetailPagerFragment.listFragment = this;
//
//		if ( !MainActivity.isPadVersion )
//		{
//			SearchContainerFragment containerFragment = (SearchContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("search");
//	    	containerFragment.switchContent(R.id.search_container, DetailPagerFragment.class.getName(), "search_pager", true);
//		}
//		else
//		{
//			DetailFragment detailFragment = (DetailFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("large_detail");
//			detailFragment.setSearchEntry(this.mAdapter.getItem(position));
//		}
//	}
//
//	@Override
//	public ArrayList<SearchEntry> getResults()
//	{
//		return this.previousResults;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data)
//	{
//		Collection<SearchEntry> searchResults = (Collection<SearchEntry>) data.get("list");
//		int newOffset = ((SearchXmlLoader) loader).getOffset();
//
//		if ( (newOffset - Constants.SEARCH_HITS_PER_REQUEST > this.previousResults.size()) )
//		{
//			this.previousResults.addAll(searchResults);
//		}
//
//		this.mAdapter.clear();
//		this.mAdapter.addAll(this.previousResults);
//		this.mAdapter.notifyDataSetChanged();
//		this.setListShown(true);
//
//		if ( this.isExpandLoading )
//		{
//			ProgressBar smallProgressBar = (ProgressBar) this.getView().findViewById(R.id.search_local_progressExtend);
//			smallProgressBar.setVisibility(View.GONE);
//
//			if ( newOffset - Constants.SEARCH_HITS_PER_REQUEST - 2 > 0)
//			{
//				this.setSelection(newOffset - Constants.SEARCH_HITS_PER_REQUEST - 2);
//			}
//		}
//
//		this.isExpandLoading = false;
//
//		// if we are on the pad version and this is the first search result, display the first entry in detail view
//		if ( MainActivity.isPadVersion && this.hits == 0 && !this.mAdapter.isEmpty() )
//		{
//			DetailFragment detailFragment = (DetailFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("large_detail");
//			detailFragment.setSearchEntry(this.mAdapter.getItem(0));
//		}
//
//		// show the number of results in action bar
//		this.hits = (Integer) data.get("numberOfRecords");
//		this.setHits(this.hits);
//
//		// dbs counting
//		if ( Constants.DBS_COUNTING_URL != null && !Constants.DBS_COUNTING_URL.isEmpty() )
//		{
//			SharedPreferences settings = this.getActivity().getPreferences(0);
//			boolean isDbsChecked = settings.getBoolean("allow_dbs", true);
//
//			if ( isDbsChecked )
//			{
//				AsyncTask<Void, Void, Void> dbsPixelTask = new DBSPixelTask();
//				dbsPixelTask.execute();
//			}
//		}
//	}
//
//	private void setHits(int hits)
//	{
//		ActionBar actionBar = this.getActivity().getActionBar();
//		Resources resources = this.getActivity().getResources();
//
//		actionBar.setSubtitle(String.valueOf(hits) + " " + resources.getString(R.string.search_hits));
//	}
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//	{
//		if ( totalItemCount > visibleItemCount & !this.isExpandLoading && totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 0) && totalItemCount < this.hits )
//		{
//			if ( this.isVisible() )
//			{
//				this.isExpandLoading = true;
//
//				ProgressBar smallProgressBar = (ProgressBar) this.getView().findViewById(R.id.search_local_progressExtend);
//				smallProgressBar.setVisibility(View.VISIBLE);
//
//				Loader<HashMap<String, Object>> loader = this.getLoaderManager().getLoader(0);
//				SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;
//
//				searchXmlLoader.forceLoad();
//			}
//		}
//	}
//
//	@Override
//	public SearchAdapter getSearchAdapter()
//	{
//		return this.mAdapter;
//	}
//
//	@Override
//	public int getHits()
//	{
//		return this.hits;
//	}
//}