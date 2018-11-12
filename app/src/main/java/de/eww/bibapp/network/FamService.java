package de.eww.bibapp.network;

import de.eww.bibapp.network.model.fam.FamResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FamService {
    @GET
    Observable<FamResult> getFam(@Url String url);
}
