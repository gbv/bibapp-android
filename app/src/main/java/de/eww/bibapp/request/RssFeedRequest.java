package de.eww.bibapp.request;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.RssFeed;
import de.eww.bibapp.model.RssItem;
import de.eww.bibapp.parser.RssXmlParser;

/**
 * Created by christoph on 24.10.14.
 */
public class RssFeedRequest extends SpiceRequest<RssFeed> {

    private String mFeedUrl;
    private Context mContext;

    public RssFeedRequest(Context context) {
        super(RssFeed.class);
        mFeedUrl = Constants.NEWS_URL;
        mContext = context;
    }

    @Override
    public RssFeed loadDataFromNetwork() throws IOException {


        RssXmlParser rssXmlParser = new RssXmlParser();
        RssFeed rssFeed = new RssFeed();

        URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(mFeedUrl, mContext);

        try {
            // open connection
            urlConnectionHelper.configure();
            urlConnectionHelper.connect(null);

            InputStream inputStream = urlConnectionHelper.getInputStream();
            List<RssItem> response = rssXmlParser.parse(inputStream);
            rssFeed.setItems(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnectionHelper.disconnect();
        }

        return rssFeed;
    }
}
