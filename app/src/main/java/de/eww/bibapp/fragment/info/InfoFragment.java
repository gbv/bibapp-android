package de.eww.bibapp.fragment.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.XmlSpringAndroidSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.activity.ContactActivity;
import de.eww.bibapp.activity.DrawerActivity;
import de.eww.bibapp.activity.ImpressumActivity;
import de.eww.bibapp.activity.LocationsActivity;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.model.RssFeed;
import de.eww.bibapp.model.RssItem;
import de.eww.bibapp.request.RssFeedRequest;

/**
 * Created by christoph on 24.10.14.
 */
public class InfoFragment extends Fragment {

    private SpiceManager mSpiceManager = new SpiceManager(XmlSpringAndroidSpiceService.class);

    private RssFeedRequest mRssFeedRequest;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mEmptyView;

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

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Do we have a rss feed to display?
        if (!Constants.NEWS_URL.isEmpty()) {
            // Start the Request
            mRssFeedRequest = new RssFeedRequest();
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSpiceManager.execute(mRssFeedRequest, new RssRequestListener());
        } else {
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mEmptyView = (TextView) view.findViewById(R.id.empty);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        Button infoButton = (Button) view.findViewById(R.id.info_button_contact);
        Button locationsButton = (Button) view.findViewById(R.id.info_button_locations);
        Button impressumButton = (Button) view.findViewById(R.id.info_button_impressum);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContactButton();
            }
        });
        locationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLocationsButton();
            }
        });
        impressumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImpressumButton();
            }
        });

        return view;
    }

    private void onClickContactButton() {
        Intent contactIntent = new Intent(getActivity(), ContactActivity.class);
        startActivity(contactIntent);
    }

    private void onClickLocationsButton() {
        Intent locationsIntent = new Intent(getActivity(), LocationsActivity.class);
        startActivityForResult(locationsIntent, 99);
    }

    private void onClickImpressumButton() {
        Intent impressumIntent = new Intent(getActivity(), ImpressumActivity.class);
        startActivity(impressumIntent);
    }

    public final class RssRequestListener implements RequestListener<RssFeed> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast toast = Toast.makeText(getActivity(), R.string.toast_info_rss_error, Toast.LENGTH_LONG);
            toast.show();

            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRequestSuccess(final RssFeed result) {
            mItemList.addAll(result.getItems());

            mProgressBar.setVisibility(View.GONE);
            if (result.getItems().isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            }

            mAdapter = new RssAdapter(mItemList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == getActivity().RESULT_OK) {
                // Set navigation position
                int navigationPosition = data.getIntExtra("navigationIndex", 0);
                ((DrawerActivity) getActivity()).selectItem(navigationPosition);
            }
        }
    }
}
