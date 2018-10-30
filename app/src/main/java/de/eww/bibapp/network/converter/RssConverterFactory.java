package de.eww.bibapp.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class RssConverterFactory extends Converter.Factory {

    public static RssConverterFactory create() {
        return new RssConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new RssResponseBodyConverter();
    }
}
