package de.eww.bibapp.network.converter;

import java.io.IOException;
import java.util.List;

import de.eww.bibapp.network.model.RssFeed;
import de.eww.bibapp.network.model.RssItem;
import de.eww.bibapp.network.parser.RssXmlParser;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class RssResponseBodyConverter implements Converter<ResponseBody, RssFeed> {

    @Override
    public RssFeed convert(ResponseBody value) throws IOException {
        RssXmlParser parser = new RssXmlParser();
        RssFeed feed = new RssFeed();

        try {
            List<RssItem> items = parser.parse(value.byteStream());
            feed.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();

            throw new IOException(e);
        }

        return feed;
    }
}
