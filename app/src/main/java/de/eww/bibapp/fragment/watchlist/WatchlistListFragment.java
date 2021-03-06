package de.eww.bibapp.fragment.watchlist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.adapter.ModsWatchlistAdapter;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.WatchlistSource;
import de.eww.bibapp.tasks.UnApiLoaderCallback;
import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 11.11.14.
 */
public class WatchlistListFragment extends RoboFragment implements
        RecyclerViewOnGestureListener.OnGestureListener,
        UnApiLoaderCallback.UnApiLoaderInterface,
        AsyncCanceledInterface,
        ActionMode.Callback {

    @Inject WatchlistSource mWatchlistSource;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mEmptyView;

    private ModsWatchlistAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mIsLoading = false;

    // The listener we are to notify when a mods item is selected
    OnModsItemSelectedListener mModsItemSelectedListener = null;

    private ActionMode mActionMode;

    private MenuItem mExportMenuItem;

    private int mRequestsDone = 0;
    private String mUnApiResponse = "";

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

        public void onEmptyList();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Get data from source
        mAdapter = new ModsWatchlistAdapter(mWatchlistSource.getModsItems("watchlist"), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar.setVisibility(View.GONE);

        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_watchlist_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mEmptyView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        if (view.getId() == R.id.list_item) {
            // Are we in action mode?
            if (mActionMode != null) {
                toggleSelection(position);
                return;
            }

            if (mModsItemSelectedListener != null) {
                mModsItemSelectedListener.onModsItemSelected(position);
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {
        // ActionMode already active?
        if (mActionMode != null) {
            return;
        }

        // Start the CAB
        mActionMode = ((RoboActionBarActivity) getActivity()).startSupportActionMode(this);
        int childPosition = mRecyclerView.getChildPosition(view);
        toggleSelection(childPosition);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.watchlist_fragment_mode_actions, menu);

        ((BaseActivity) getActivity()).showToolbar(false);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_remove_from_watchlist:
                List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
                int currentPosition;

                removeItems();

                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currentPosition = selectedItemPositions.get(i);
                    mAdapter.removeModsItem(currentPosition);
                }

                if (mAdapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mModsItemSelectedListener.onEmptyList();
                }

                mActionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;
        mAdapter.clearSelection();

        ((BaseActivity) getActivity()).showToolbar(true);

    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        String title = getString(R.string.menu_selected_count, mAdapter.getSelectedItemCount());
        mActionMode.setTitle(title);
    }

    private void removeItems() {
        // get actual watchlist
        ArrayList<ModsItem> watchlistEntries = new ArrayList<ModsItem>();

        File file = getActivity().getFileStreamPath("watchlist");
        if (file.isFile()) {
            try {
                FileInputStream fis = getActivity().openFileInput("watchlist");
                ObjectInputStream ois = new ObjectInputStream(fis);
                watchlistEntries = (ArrayList<ModsItem>) ois.readObject();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Remove entries and save watchlist
        List<Integer> selectedItems = mAdapter.getSelectedItems();
        for(int i : selectedItems) {
            ModsItem modsItem = mAdapter.getModsItem(i);
            watchlistEntries.remove(modsItem);
        }

        try {
            FileOutputStream fos = getActivity().openFileOutput("watchlist", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(watchlistEntries);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display toast
        Toast toast = Toast.makeText(getActivity(), R.string.toast_watchlist_removed, Toast.LENGTH_LONG);
        toast.show();

        // refresh menu
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.watchlist_fragment_actions, menu);

        mExportMenuItem = menu.findItem(R.id.menu_export_from_watchlist);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // enable export button, if we have some items
        if (mAdapter.getItemCount() > 0) {
            mExportMenuItem.setVisible(true);
        } else {
            mExportMenuItem.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_export_from_watchlist:

                mRequestsDone = 0;
                mUnApiResponse = "";
                performUnApiRequests();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void performUnApiRequests() {
        int totalRequests = mAdapter.getItemCount();

        if (mRequestsDone < totalRequests) {
            ModsItem modsItem = mAdapter.getModsItem(mRequestsDone);
            mUnApiResponse += Integer.toString(mRequestsDone + 1) + ") " + modsItem.title + " - ";
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.destroyLoader(0);
            loaderManager.initLoader(0, null, new UnApiLoaderCallback(this, modsItem));
        } else {
            // create an intent to send an email with a list of watchlist items
            Intent sendIntent = new Intent(Intent.ACTION_SEND);

            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.watchlist_export_subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mUnApiResponse);

            PackageManager packageManager = getActivity().getPackageManager();
            List activities = packageManager.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (activities.size() > 0) {
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.watchlist_export_send_to)));
            }
        }
    }

    @Override
    public void onUnApiRequestDone(String authorExtended) {
        mRequestsDone++;
        mUnApiResponse += authorExtended + "\n";

        performUnApiRequests();
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_mods_error, Toast.LENGTH_LONG);
        toast.show();
    }
}