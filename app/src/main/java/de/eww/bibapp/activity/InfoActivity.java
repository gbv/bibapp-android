package de.eww.bibapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.model.RssFeed;
import de.eww.bibapp.model.RssItem;
import de.eww.bibapp.request.RssFeedRequest;

public class InfoActivity extends BaseActivity {

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
        mSpiceManager.start(this);
    }

    @Override
    public void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mEmptyView = (TextView) findViewById(R.id.empty);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Do we have a rss feed to display?
        if (!Constants.NEWS_URL.isEmpty()) {
            // Start the Request
            mRssFeedRequest = new RssFeedRequest(this);
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSpiceManager.execute(mRssFeedRequest, new RssRequestListener());
        } else {
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }

        Button infoButton = (Button) findViewById(R.id.info_button_contact);
        Button locationsButton = (Button) findViewById(R.id.info_button_locations);
        Button impressumButton = (Button) findViewById(R.id.info_button_impressum);

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
    }

    private void onClickContactButton() {
        Intent contactIntent = new Intent(this, ContactActivity.class);
        startActivityForResult(contactIntent, 99);
    }

    private void onClickLocationsButton() {
        Intent locationsIntent = new Intent(this, LocationsActivity.class);
        startActivityForResult(locationsIntent, 99);
    }

    private void onClickImpressumButton() {
        Intent impressumIntent = new Intent(this, ImpressumActivity.class);
        startActivityForResult(impressumIntent, 99);
    }

    public final class RssRequestListener implements RequestListener<RssFeed> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast toast = Toast.makeText(InfoActivity.this, R.string.toast_info_rss_error, Toast.LENGTH_LONG);
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
            if (resultCode == this.RESULT_OK) {
                // Set navigation position
                int navigationPosition = data.getIntExtra("navigationIndex", 0);
                ((BaseActivity) this).selectItem(navigationPosition);
            }
        }
    }
}
