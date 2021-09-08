package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.SruResult;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SruService {
    @GET
    @Annotations.SRU
    Single<SruResult> getSearchResult(@Url String url);
}
