package de.eww.bibapp.network;

import de.eww.bibapp.network.converter.Annotations;
import de.eww.bibapp.network.model.paia.PaiaFees;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.network.model.paia.PaiaLogin;
import de.eww.bibapp.network.model.paia.PaiaLogout;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaiaService {

    @GET("auth/login")
    @Annotations.Json
    Single<PaiaLogin> login(
            @Query("username") String username,
            @Query("password") String password,
            @Query("grant_type") String grantType);

    @GET("auth/logout")
    @Annotations.Json
    Single<PaiaLogout> logout(
            @Query("patron") String patron,
            @Query("access_token") String accessToken);

    @GET("core/{username}")
    @Annotations.Json
    Single<PaiaPatron> patron(
            @Path("username") String username,
            @Query("access_token") String accessToken);

    @GET("core/{username}/items")
    @Annotations.Json
    Single<PaiaItems> borrowed(
            @Path("username") String username,
            @Query("access_token") String accessToken);

    @GET("core/{username}/fees")
    @Annotations.Json
    Single<PaiaFees> fees(
            @Path("username") String username,
            @Query("access_token") String accessToken);

    @POST("core/{username}/renew")
    @Annotations.Json
    Single<PaiaItems> renew(
            @Path("username") String username,
            @Query("access_token") String accessToken,
            @Body PaiaItems items);

    @POST("core/{username}/request")
    @Annotations.Json
    Single<PaiaItems> request(
            @Path("username") String username,
            @Query("access_token") String accessToken,
            @Body PaiaItems items);

    @POST("core/{username}/cancel")
    @Annotations.Json
    Single<PaiaItems> cancel(
            @Path("username") String username,
            @Query("access_token") String accessToken,
            @Body PaiaItems items);
}
