package de.eww.bibapp;

import android.app.Application;

import de.eww.bibapp.typeface.BeluginoFont;

import com.mikepenz.iconics.Iconics;

/**
 * Created by cschoenf on 07.02.17.
 */

public class BibAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Iconics
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new BeluginoFont());
    }
}
