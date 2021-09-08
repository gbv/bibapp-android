package de.eww.bibapp.network.converter;

import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonConverterFactory extends Converter.Factory {

    final private Converter.Factory jsonConverter;

    public static JsonConverterFactory create(Converter.Factory jsonConverter) {
        return new JsonConverterFactory(jsonConverter);
    }

    public JsonConverterFactory(Converter.Factory jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation: annotations) {
            if (annotation instanceof Annotations.Json) {
                return jsonConverter.responseBodyConverter(type, annotations, retrofit);
            }
        }

        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        for (Annotation annotation: methodAnnotations) {
            if (annotation instanceof Annotations.Json) {
                return jsonConverter.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            }
        }

        return null;
    }
}
