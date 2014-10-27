package de.eww.bibapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.io.IOException;

import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.RssFeed;
import roboguice.util.Ln;

/**
 * Created by christoph on 24.10.14.
 */
public class RssFeedRequest extends SpringAndroidSpiceRequest<RssFeed> {

    private String mFeedUrl;

    public RssFeedRequest() {
        super(RssFeed.class);
        this.mFeedUrl = Constants.NEWS_URL;
    }

    @Override
    public RssFeed loadDataFromNetwork() throws IOException {
        Ln.d("Request RSS feed " + mFeedUrl);

        return getRestTemplate().getForObject(mFeedUrl, RssFeed.class);
    }
}
