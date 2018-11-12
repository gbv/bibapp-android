package de.eww.bibapp.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class DaiaConverterFactory extends Converter.Factory {

    public static DaiaConverterFactory create() {
        return new DaiaConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation: annotations) {
            if (annotation instanceof Annotations.DAIA) {
                return new DaiaResponseBodyConverter();
            }
        }

        return null;
    }
}
