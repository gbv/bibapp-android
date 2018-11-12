package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.RssFeed;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RssService {
    @GET
    @Annotations.RSS
    Single<RssFeed> getRss(@Url String url);
}
