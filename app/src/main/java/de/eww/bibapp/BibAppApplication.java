package de.eww.bibapp;

import androidx.multidex.MultiDexApplication;

import de.eww.bibapp.typeface.BeluginoFont;

import com.mikepenz.iconics.Iconics;

/**
 * Created by cschoenf on 07.02.17.
 */

public class BibAppApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Iconics
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new BeluginoFont());
    }
}
