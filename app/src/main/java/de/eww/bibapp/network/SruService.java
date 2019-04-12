package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.SruResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SruService {
    @GET
    @Annotations.SRU
    Observable<SruResult> getSearchResult(@Url String url);
}
