package de.eww.bibapp.model.source;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.model.ModsItem;

/**
 * Created by christoph on 11.11.14.
 */
public class WatchlistSource extends ModsSource {

    public static void loadFromFile(Context context) {
        List<ModsItem> response = new ArrayList<ModsItem>();

        try {
            File file = context.getFileStreamPath("watchlist");
            if (file.isFile()) {
                FileInputStream fis = context.openFileInput("watchlist");

                ObjectInputStream ois = new ObjectInputStream(fis);
                response = (ArrayList<ModsItem>) ois.readObject();

                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addModsItems("watchlist", response);
        setTotalItems("watchlist", response.size());
    }
}
