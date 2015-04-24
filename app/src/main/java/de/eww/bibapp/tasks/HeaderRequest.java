package de.eww.bibapp.tasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.activity.WebViewActivity;

public class HeaderRequest extends AsyncTask<String, Void, Map<String, List<String>>> {
    WebViewActivity mActivity = null;

	public HeaderRequest(WebViewActivity activity) {
        mActivity = activity;
	}

	protected Map<String, List<String>> doInBackground(String... urls) {
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(urls[0], mActivity);

        Map<String, List<String>> header = new HashMap<String, List<String>>();

		try {
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);

            header = urlConnectionHelper.getHeader();
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		finally {
			urlConnectionHelper.disconnect();
		}

        return header;
	}

    protected void onPostExecute(Map<String, List<String>> header) {
        mActivity.onHeaderRequestDone(header);
    }
}
