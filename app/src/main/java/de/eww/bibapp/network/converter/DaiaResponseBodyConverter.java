package de.eww.bibapp.network.converter;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.DaiaItems;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class DaiaResponseBodyConverter implements Converter<ResponseBody, DaiaItems> {
    @Override
    public DaiaItems convert(@NonNull ResponseBody value) throws IOException {
        ArrayList<DaiaItem> daiaResponse = new ArrayList<>();

        try {
            JSONObject daiaJsonResponse = new JSONObject(value.string());
            if (daiaJsonResponse.has("document")) {
                JSONArray documentArray = daiaJsonResponse.getJSONArray("document");

                for (int i=0; i < documentArray.length(); i++) {
                    JSONObject documentObject = documentArray.getJSONObject(i);

                    if (documentObject.has("item")) {
                        JSONArray itemArray = documentObject.getJSONArray("item");

                        for (int j=0; j < itemArray.length(); j++) {
                            JSONObject itemObject = itemArray.getJSONObject(j);

                            DaiaItem daiaItem = new DaiaItem(itemObject);
                            daiaResponse.add(daiaItem);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            throw new IOException(e);
        }

        DaiaItems items = new DaiaItems();
        items.setItems(daiaResponse);

        return items;
    }
}
