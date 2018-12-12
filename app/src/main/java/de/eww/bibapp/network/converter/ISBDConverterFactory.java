package de.eww.bibapp.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ISBDConverterFactory extends Converter.Factory {

    public static ISBDConverterFactory create() {
        return new ISBDConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation: annotations) {
            if (annotation instanceof Annotations.ISBD) {
                return new ISBDResponseBodyConverter();
            }
        }

        return null;
    }
}