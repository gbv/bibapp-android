//package de.eww.bibapp.tasks;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.io.BufferedInputStream;
//import java.io.InputStream;
//
//import de.eww.bibapp.URLConnectionHelper;
//import de.eww.bibapp.constants.Constants;
//
//public class DBSPixelTask extends AsyncTask<Void, Void, Void>
//{
//	public DBSPixelTask()
//	{
//	}
//
//	@Override
//	protected Void doInBackground(Void... params)
//	{
//		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.DBS_COUNTING_URL);
//
//		try
//		{
//			urlConnectionHelper.configure();
//			urlConnectionHelper.connect(null);
//
//			InputStream input = new BufferedInputStream(urlConnectionHelper.getInputStream());
//
//			String httpResponse = urlConnectionHelper.readStream(input);
//			Log.v("DBS", httpResponse);
//
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
//		return null;
//	}
//}
