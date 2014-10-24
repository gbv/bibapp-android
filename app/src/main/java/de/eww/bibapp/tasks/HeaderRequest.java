//package de.eww.bibapp.tasks;
//
//import android.os.AsyncTask;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.eww.bibapp.URLConnectionHelper;
//import de.eww.bibapp.fragments.detail.WebViewFragment;
//
//public class HeaderRequest extends AsyncTask<String, Void, Map<String, List<String>>>
//{
//    WebViewFragment fragment = null;
//
//	public HeaderRequest(WebViewFragment fragment) {
//        this.fragment = fragment;
//	}
//
//	protected Map<String, List<String>> doInBackground(String... urls) {
//		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(urls[0]);
//
//        Map<String, List<String>> header = new HashMap<String, List<String>>();
//
//		try
//		{
//			urlConnectionHelper.configure();
//			urlConnectionHelper.connect(null);
//
//            header = urlConnectionHelper.getHeader();
//		}
//		catch ( Exception e )
//		{
//			e.printStackTrace();
//		}
//
//		finally
//		{
//			urlConnectionHelper.disconnect();
//		}
//
//        return header;
//	}
//
//    protected void onPostExecute(Map<String, List<String>> header) {
//        this.fragment.onHeaderRequestDone(header);
//    }
//}
