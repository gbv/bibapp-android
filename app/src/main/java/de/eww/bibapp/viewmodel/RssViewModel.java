package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.eww.bibapp.R;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.RssService;
import de.eww.bibapp.network.model.RssFeed;
import de.eww.bibapp.network.model.StatefullData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class RssViewModel extends AndroidViewModel {
    private MutableLiveData<StatefullData<RssFeed>> feed;

    private CompositeDisposable disposable = new CompositeDisposable();

    public RssViewModel(Application application) {
        super(application);
    }

    public LiveData<StatefullData<RssFeed>> getFeed() {
        if (feed == null) {
            feed = new MutableLiveData<>();
            loadFeed();
        }

        return feed;
    }

    public LiveData<StatefullData<RssFeed>> refreshFeed() {
        loadFeed();

        return feed;
    }

    private void loadFeed() {
        RssService service = ApiClient.getClient(getApplication(), HttpUrl.parse("http://dummy.de/")).create(RssService.class);
        String rssUrl = getApplication().getResources().getString(R.string.bibapp_rss_url);
        disposable.add(service
                .getRss(rssUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RssFeed>() {
                    @Override
                    public void onSuccess(RssFeed rssFeed) {
                        feed.setValue(new StatefullData<>(rssFeed, false));
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        feed.setValue(new StatefullData<>(null, true));
                    }
                })
        );
    }
}
