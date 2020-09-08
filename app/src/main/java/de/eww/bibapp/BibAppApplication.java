package de.eww.bibapp;

import android.app.Application;

import com.mikepenz.iconics.Iconics;

import de.eww.bibapp.typeface.BeluginoFont;

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
