package de.eww.bibapp.network.source;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.network.model.ModsItem;

public class WatchlistSource extends ModsSource {

    public static void loadFromFile(Context context) {
        List<ModsItem> response = new ArrayList<>();

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

    public static void storeInFile(Context context, List<ModsItem> modsItems) {
        try {
            FileOutputStream fos = context.openFileOutput("watchlist", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(modsItems);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
