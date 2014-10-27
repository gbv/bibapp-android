package de.eww.bibapp.activity;

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
@ContentView(R.layout.activity_contacts)
public class ContactActivity extends RoboActivity {

    @InjectView(R.id.contact) TextView mTextView;

    @InjectResource(R.string.contact_text) String mContactText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextView.setText(Html.fromHtml(mContactText));
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Linkify
        Linkify.addLinks(mTextView, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(mTextView, Linkify.PHONE_NUMBERS);
    }
}
