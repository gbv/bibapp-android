package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.DaiaItems;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DaiaService {
    @GET
    @Annotations.DAIA
    Observable<DaiaItems> getDaia(@Url String url);

    @GET
    Observable<String> getDaiaSub(@Url String url);
}
