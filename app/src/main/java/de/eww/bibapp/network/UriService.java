package de.eww.bibapp.network;

import org.json.JSONObject;

import de.eww.bibapp.network.converter.Annotations;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface UriService {
    @GET
    @Annotations.URI
    Observable<JSONObject> getLocations(@Url String url);

    @GET
    @Annotations.URI
    Observable<JSONObject> getLocation(@Url String url);
}
