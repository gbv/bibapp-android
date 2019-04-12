package de.eww.bibapp.network.converter;

import java.io.IOException;

import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.parser.SearchXmlParser;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class SruResponseBodyConverter implements Converter<ResponseBody, SruResult> {

    @Override
    public SruResult convert(ResponseBody value) throws IOException {
        SearchXmlParser parser = new SearchXmlParser();
        SruResult result = new SruResult();

        try {
            result.setResult(parser.parse(value.byteStream()));
        } catch (Exception e) {
            e.printStackTrace();

            throw new IOException(e);
        }

        return result;
    }
}
