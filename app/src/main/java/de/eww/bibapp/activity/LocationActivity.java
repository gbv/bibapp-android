package de.eww.bibapp.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.info.LocationFragment;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */

/**
 * Activity that displays a particular location onscreen.
 *
 * This activity is started only when the screen in not large enough for a two-pane layout, in
 * which case this separate activity is shown in order to display the location. This activity
 * kills itself if the display is reconfigured into a shape that allows a two-pane layout, since
 * in that case the location article will be displayed by the {@link de.eww.bibapp.activity.LocationsActivity}
 * and this Activity becomes unnecessary.
 */
public class LocationActivity extends RoboFragmentActivity {

    // The location index we are to display
    int mLocationIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationIndex = getIntent().getExtras().getInt("locationIndex", 0);

        // If we are in two-pane layout mode, this activity is no longer necessary
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            finish();
            return;
        }

        // Place a LocationFragment as our content pane
        LocationFragment locationFragment = new LocationFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, locationFragment).commit();

        // Display the correct location on the fragment
        //NewsArticle article = NewsSource.getInstance().getCategory(mCatIndex).getArticle(mArtIndex);
        //f.displayArticle(article);
    }
}
