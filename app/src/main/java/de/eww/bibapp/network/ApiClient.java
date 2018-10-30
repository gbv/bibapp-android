package de.eww.bibapp.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import de.eww.bibapp.network.converter.RssConverterFactory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;

    public static Retrofit getClient(Context context, HttpUrl baseUrl) {
        if (ApiClient.okHttpClient == null) {
            ApiClient.initOkHttp(context);
        }

        if (ApiClient.retrofit == null) {
            ApiClient.retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(ApiClient.okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(RssConverterFactory.create())
                    .build();
        }

        return ApiClient.retrofit;
    }

    private static void initOkHttp(final Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(loggingInterceptor);

        ApiClient.okHttpClient = httpClient.build();
    }
}
