package de.eww.bibapp.fragment.info;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.RssItem;
import de.eww.bibapp.model.RssFeed;
import de.eww.bibapp.request.RssFeedRequest;

/**
 * Created by christoph on 24.10.14.
 */
public class InfoFragment extends Fragment {

    private SpiceManager mSpiceManager = new SpiceManager(XmlSpringAndroidSpiceService.class);

    private RssFeedRequest mRssFeedRequest;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;

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

        // Do we have a rss feed to display?
        if (!Constants.NEWS_URL.isEmpty()) {
            // Start the Request
            mProgressBar.setVisibility(View.VISIBLE);
            mRssFeedRequest = new RssFeedRequest();
            mSpiceManager.execute(mRssFeedRequest, new RssRequestListener());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.info_rss_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        // Check if a homepage url is given
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

        if (Constants.HOMEPAGE_URLS.length >= localCatalogIndex + 1) {
            Button homepageButton = (Button) view.findViewById(R.id.info_button_homepage);
            homepageButton.setVisibility(View.VISIBLE);
        }

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

        Uri homepageUrl = Uri.parse(Constants.HOMEPAGE_URLS[localCatalogIndex]);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, homepageUrl);
        startActivity(launchBrowser);
    }

    public final class RssRequestListener implements RequestListener<RssFeed> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast toast = Toast.makeText(getActivity(), R.string.toast_info_rss_error, Toast.LENGTH_LONG);
            toast.show();

            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final RssFeed result) {
            mItemList.addAll(result.getItems());

            mProgressBar.setVisibility(View.GONE);

            mAdapter = new RssAdapter(mItemList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
