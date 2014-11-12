package de.eww.bibapp.fragment.watchlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsWatchlistAdapter;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.source.WatchlistSource;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 11.11.14.
 */
public class WatchlistListFragment extends RoboFragment implements
        RecyclerViewOnGestureListener.OnGestureListener {

    @Inject WatchlistSource mWatchlistSource;

    RecyclerView mRecyclerView;

    private ModsWatchlistAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mIsLoading = false;

    // The listener we are to notify when a mods item is selected
    OnModsItemSelectedListener mModsItemSelectedListener = null;

    private MenuItem mMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    /**
     * Represents a listener that will be notified of mods item selections.
     */
    public interface OnModsItemSelectedListener {
        /**
         * Call when a given mods item is selected.
         *
         * @param index the index of the selected mods item.
         */
        public void onModsItemSelected(int index);
    }

    /**
     * Sets the listener that should be notified of mods item selection events.
     *
     * @param listener the listener to notify.
     */
    public void setOnModsItemSelectedListener(OnModsItemSelectedListener listener) {
        mModsItemSelectedListener = listener;
    }

    public void setSelection(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Improve performance for RecyclerView by setting it to a fixed size,
        // since we now that changes in content do not change the layout size
        // of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Get data from source
        mAdapter = new ModsWatchlistAdapter(mWatchlistSource.getModsItems(), getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_watchlist_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.watchlist_results);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the toolbar
        inflater.inflate(R.menu.watchlist_fragment_actions, menu);

        mMenuItem = menu.findItem(R.id.menu_remove_from_watchlist);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mMenuItem.setEnabled(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove_from_watchlist:
                removeItemFromList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view, int position) {
        if (view.getId() == R.id.watchlist_list_item) {
            if (mModsItemSelectedListener != null) {
                mModsItemSelectedListener.onModsItemSelected(position);
            }

            toggleSelection(position);
        }
    }

    @Override
    public void onLongPress(View view, int position) {

    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);

    }

    private void removeItemFromList() {

    }
}


//			final int finalPostion = position;
//			checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//			{
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//				{
//					// update checked items
//					SearchEntry searchItem = (SearchEntry) SearchAdapter.this.watchlistFragment.getListAdapter().getItem(finalPostion);
//					if ( isChecked )
//					{
//						SearchAdapter.this.watchlistFragment.checkedItems.add(searchItem);
//					}
//					else
//					{
//						SearchAdapter.this.watchlistFragment.checkedItems.remove(searchItem);
//					}
//
//					// enable / disable menu item
//					SearchAdapter.this.watchlistFragment.menuItem.setEnabled(!SearchAdapter.this.watchlistFragment.checkedItems.isEmpty());
//				}
//			});



//
//public class WatchlistFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<List<SearchEntry>>,
//	SearchAdapterInterface
//{
//	// This is the Adapter being used to display the list's data.
//    SearchAdapter mAdapter;
//
//    public ArrayList<SearchEntry> checkedItems;
//    public MenuItem menuItem;
//    private ArrayList<SearchEntry> previousResults = new ArrayList<SearchEntry>();
//
//
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		switch ( item.getItemId() )
//	    {
//	        case R.id.menu_watchlist_remove:
//	        	// get actual watchlist
//	        	ArrayList<SearchEntry> watchlistEntries = new ArrayList<SearchEntry>();
//
//	        	File file = this.getActivity().getFileStreamPath("watchlist");
//	        	if ( file.isFile() )
//	        	{
//	        		try
//	        		{
//		    			FileInputStream fis = this.getActivity().openFileInput("watchlist");
//
//		    			ObjectInputStream ois = new ObjectInputStream(fis);
//		    			watchlistEntries = (ArrayList<SearchEntry>) ois.readObject();
//
//		    			fis.close();
//		    		}
//		    		catch (Exception e)
//		    		{
//		    			// TODO Auto-generated catch block
//		    			e.printStackTrace();
//		    		}
//	        	}
//
//	        	// remove entries and save watchlist
//	        	Iterator<SearchEntry> it = this.checkedItems.iterator();
//
//	        	while ( it.hasNext() )
//	        	{
//	        		SearchEntry checkedItem = it.next();
//	        		watchlistEntries.remove(checkedItem);
//
//					this.mAdapter.remove(checkedItem);
//	        	}
//
//	        	try
//				{
//					FileOutputStream fos = this.getActivity().openFileOutput("watchlist", Context.MODE_PRIVATE);
//					ObjectOutputStream oos = new ObjectOutputStream(fos);
//					oos.writeObject(watchlistEntries);
//					oos.close();
//				}
//				catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//	        	// display toast
//				Context context = this.getActivity().getApplicationContext();
//				Resources resource = this.getActivity().getResources();
//
//				Toast toast = Toast.makeText(context, resource.getText(R.string.toast_watchlist_removed), Toast.LENGTH_SHORT);
//				toast.show();
//
//				// reset checked items
//				this.checkedItems.clear();
//				this.mAdapter.notifyDataSetChanged();
//
//				// reset menu item
//				this.menuItem.setEnabled(false);
//
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}