package de.eww.bibapp.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SruConverterFactory extends Converter.Factory {

    public static SruConverterFactory create() {
        return new SruConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation: annotations) {
            if (annotation instanceof Annotations.SRU) {
                return new SruResponseBodyConverter();
            }
        }

        return null;
    }
}
