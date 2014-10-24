package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.data.RssItem;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 24.10.14.
 */
public class InfoFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<List<RssItem>>,
        AsyncCanceledInterface {

    @InjectView(R.id.info_rss_view) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<RssItem> mItemList = new ArrayList<RssItem>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Improve performance for RecyclerView by setting it to a fixed size,
        // since we now that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Specify an adapter
        mAdapter = new RssAdapter(mItemList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        return view;
    }
}
