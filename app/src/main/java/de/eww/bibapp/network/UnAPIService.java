package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.ISBD;
import de.eww.bibapp.network.model.pica.PicaRecord;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface UnAPIService {
    @GET
    @Annotations.ISBD
    Single<ISBD> getISBD(@Url String url);

    @GET
    Observable<PicaRecord> getPica(@Url String url);
}
