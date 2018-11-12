package de.eww.bibapp.network.converter;

import java.io.IOException;

import de.eww.bibapp.network.model.ISBD;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ISBDResponseBodyConverter implements Converter<ResponseBody, ISBD> {

    @Override
    public ISBD convert(ResponseBody value) throws IOException {
        String[] split;

        try {
            String content = value.string();

            split = content.split(System.getProperty("line.separator"));
        } catch (Exception e) {
            e.printStackTrace();

            throw new IOException(e);
        }

        ISBD isbd = new ISBD();
        isbd.setLines(split);
        return isbd;
    }
}
