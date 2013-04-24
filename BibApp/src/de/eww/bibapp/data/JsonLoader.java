package de.eww.bibapp.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import de.eww.bibapp.constants.Constants;

public final class JsonLoader extends AsyncTaskLoader<JSONObject>
{
	JSONObject json;

	public JsonLoader(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onStartLoading()
	{
		if ( this.json != null )
		{
			this.deliverResult(this.json);
		}
		
		if ( this.takeContentChanged() || this.json == null )
		{
			this.forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading()
	{
		this.cancelLoad();
	}
	
	@Override
	protected void onReset()
	{
		super.onReset();
		
		this.onStopLoading();
		
		this.json = null;
	}

	@Override
	public JSONObject loadInBackground()
	{
		JSONObject response = new JSONObject();
		
		try
		{
			URL url = new URL(Constants.PAIA_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			
			try
			{
				InputStream input = new BufferedInputStream(urlConnection.getInputStream());
				String httpResponse = this.readStream(input);
				
				Log.v("test", httpResponse);
			}
			finally
			{
				urlConnection.disconnect();
			}
		}
		catch ( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	private String readStream(InputStream input)
	{
		Writer writer = new StringWriter();
		
		try
		{
			InputStreamReader streamReader = new InputStreamReader(new BufferedInputStream(input), "UTF-8");
			
			try
			{
				final char[] buffer = new char[1024];
				int read;
				
				while ( (read = streamReader.read(buffer)) != -1 )
				{
					writer.write(buffer, 0, read);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					streamReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}
		
		return writer.toString();
	}
}