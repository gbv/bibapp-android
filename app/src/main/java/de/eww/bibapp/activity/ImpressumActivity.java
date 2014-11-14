package de.eww.bibapp.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import de.eww.bibapp.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */
@ContentView(R.layout.activity_impressum)
public class ImpressumActivity extends RoboActivity {

    @InjectView(R.id.impressum) TextView mTextView;

    @InjectResource(R.string.impressum_text) String mImpressumText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set orientation
        Resources resources = getResources();
        boolean isLandscape = resources.getBoolean(R.bool.landscape);
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mTextView.setText(Html.fromHtml(mImpressumText));
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Linkify
        Linkify.addLinks(mTextView, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(mTextView, Linkify.PHONE_NUMBERS);
    }
}
