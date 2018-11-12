package de.eww.bibapp;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cschoenf on 07.02.17.
 */

public class BibAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}
