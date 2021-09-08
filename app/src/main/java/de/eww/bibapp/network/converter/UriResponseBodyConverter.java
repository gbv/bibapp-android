package de.eww.bibapp.network.converter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class UriResponseBodyConverter implements Converter<ResponseBody, JSONObject> {

    @Override
    public JSONObject convert(ResponseBody value) throws IOException {
        try {
            return new JSONObject(value.string());
        } catch (JSONException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
    }
}
