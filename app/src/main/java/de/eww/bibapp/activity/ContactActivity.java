package de.eww.bibapp.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import de.eww.bibapp.R;

/**
 * Created by christoph on 25.10.14.
 */
public class ContactActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        TextView textView = (TextView) findViewById(R.id.contact);

        Resources resources = getResources();

        textView.setMovementMethod(LinkMovementMethod.getInstance());   // needs to be called before setText
        textView.setText(Html.fromHtml(resources.getString(R.string.contact_text)));

        // Linkify
        Linkify.addLinks(textView, Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);

        setActiveNavigationItem(3);
    }
}
