package de.eww.bibapp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;
import de.eww.bibapp.constants.Constants;

public class URLConnectionHelper
{
	public String urlString;
	private URL url;
	private HttpURLConnection connection;
	
	public URLConnectionHelper(String url)
	{
		this.urlString = url;
	}
	
	public void configure() throws URISyntaxException, MalformedURLException
	{
		this.url = new URL(this.urlString);
		
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		this.url = uri.toURL();
	}
	
	public void connect(String postData) throws Exception
	{
		Log.v("URLConnectionHelper", this.url.toExternalForm());
		
		if ( this.url.getProtocol().equals("http") )
		{
			this.connection = (HttpURLConnection) this.url.openConnection();
		}
		else
		{
			this.connection = (HttpsURLConnection) this.url.openConnection();
		}
		
		this.connection.setReadTimeout(Constants.READ_TIMEOUT);
		this.connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
		this.connection.setDoInput(true);
		
		if ( postData == null )
		{
			this.connection.setRequestMethod("GET");
		}
		else
		{
			this.connection.setRequestMethod("POST");
			this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			this.connection.setRequestProperty("Content-Length", String.valueOf(postData.length()));
			this.connection.setDoOutput(true);
			this.connection.setUseCaches(false);
		}
		
		this.connection.connect();
		
		if ( postData != null )
		{
			DataOutputStream outputStream = new DataOutputStream(this.connection.getOutputStream());
			Log.v("TO PAIA", postData);
			outputStream.writeBytes(postData);
			outputStream.flush();
			outputStream.close();
		}
	}
	
	public InputStream getInputStream() throws Exception
	{
		return this.connection.getInputStream();
	}
	
	public int getResponseCode() throws Exception
	{
		return this.connection.getResponseCode();
	}
	
	public InputStream getErrorStream()
	{
		return this.connection.getErrorStream();
	}
	
	public void disconnect()
	{
		this.connection.disconnect();
	}
	
	public InputStream getStream() throws Exception
	{
		if ( this.getResponseCode() != 200 )
		{
			return this.getErrorStream();
		}
		else
		{
			return this.getInputStream();
		}
	}
	
	public String readStream(InputStream input)
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
