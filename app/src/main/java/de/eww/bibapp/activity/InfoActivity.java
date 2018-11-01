package de.eww.bibapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.RssService;
import de.eww.bibapp.network.model.RssFeed;
import de.eww.bibapp.network.model.RssItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class InfoActivity extends BaseActivity {

    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.recycler) RecyclerView recyclerView;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.empty) TextView emptyView;

    private RecyclerView.Adapter mAdapter;

    private List<RssItem> mItemList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        // Use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        this.recyclerView.addItemDecoration(itemDecoration);

        // Do we have a rss feed to display?
        //noinspection ConstantConditions
        if (!Constants.NEWS_URL.isEmpty()) {
            // Start the Request
            this.emptyView.setVisibility(View.GONE);
            this.progressBar.setVisibility(View.VISIBLE);

            RssService service = ApiClient.getClient(this.getApplicationContext(), HttpUrl.parse("http://dummy.de/")).create(RssService.class);
            this.disposable.add(service
                    .getRss(Constants.NEWS_URL)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<RssFeed>() {
                        @Override
                        public void onSuccess(RssFeed feed) {
                            mItemList.addAll(feed.getItems());

                            InfoActivity.this.progressBar.setVisibility(View.GONE);
                            if (feed.getItems().isEmpty()) {
                                InfoActivity.this.emptyView.setVisibility(View.VISIBLE);
                            }

                            mAdapter = new RssAdapter(mItemList);
                            InfoActivity.this.recyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast toast = Toast.makeText(InfoActivity.this, R.string.toast_info_rss_error, Toast.LENGTH_LONG);
                            toast.show();

                            InfoActivity.this.progressBar.setVisibility(View.GONE);
                            InfoActivity.this.emptyView.setVisibility(View.VISIBLE);
                        }
                    }));
        } else {
            this.emptyView.setVisibility(View.GONE);
            this.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        this.disposable.dispose();
        super.onDestroy();
    }

    @OnClick(R.id.info_button_contact)
    public void onClickContactButton() {
        Intent contactIntent = new Intent(this, ContactActivity.class);
        startActivityForResult(contactIntent, 99);
    }

    @OnClick(R.id.info_button_locations)
    public void onClickLocationsButton() {
        Intent locationsIntent = new Intent(this, LocationsActivity.class);
        startActivityForResult(locationsIntent, 99);
    }

    @OnClick(R.id.info_button_impressum)
    public void onClickImpressumButton() {
        Intent impressumIntent = new Intent(this, ImpressumActivity.class);
        startActivityForResult(impressumIntent, 99);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == Activity.RESULT_OK) {
                // Set navigation position
                int navigationPosition = data.getIntExtra("navigationIndex", 0);
                this.selectItem(navigationPosition);
            }
        }
    }
}
