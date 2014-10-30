package de.eww.bibapp.fragment.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.XmlSpringAndroidSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.ContactActivity;
import de.eww.bibapp.activity.ImpressumActivity;
import de.eww.bibapp.activity.LocationsActivity;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.model.RssItem;
import de.eww.bibapp.model.RssFeed;
import de.eww.bibapp.request.RssFeedRequest;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 24.10.14.
 */
public class InfoFragment extends RoboFragment {

    private SpiceManager mSpiceManager = new SpiceManager(XmlSpringAndroidSpiceService.class);

    private RssFeedRequest mRssFeedRequest;

    @InjectView(R.id.info_rss_view) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<RssItem> mItemList = new ArrayList<RssItem>();

    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

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

        // Start the Request
        mRssFeedRequest = new RssFeedRequest();
        getActivity().setProgressBarIndeterminate(false);
        getActivity().setProgressBarVisibility(true);
        mSpiceManager.execute(mRssFeedRequest, new RssRequestListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @OnClick(R.id.info_button_contact) void onClickContactButton() {
        Intent contactIntent = new Intent(getActivity(), ContactActivity.class);
        startActivity(contactIntent);
    }

    @OnClick(R.id.info_button_locations) void onClickLocationsButton() {
        Intent locationsIntent = new Intent(getActivity(), LocationsActivity.class);
        startActivity(locationsIntent);
    }

    @OnClick(R.id.info_button_impressum) void onClickImpressumButton() {
        Intent impressumIntent = new Intent(getActivity(), ImpressumActivity.class);
        startActivity(impressumIntent);
    }

    @OnClick(R.id.info_button_homepage) void onClickHomepageButton() {

    }

    public final class RssRequestListener implements RequestListener<RssFeed> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // TODO: Toast
        }

        @Override
        public void onRequestSuccess(final RssFeed result) {
            // TODO: Toast
            mItemList.addAll(result.getItems());

            getActivity().setProgressBarVisibility(false);

            mAdapter = new RssAdapter(mItemList);
            mRecyclerView.setAdapter(mAdapter);

        }
    }
}
