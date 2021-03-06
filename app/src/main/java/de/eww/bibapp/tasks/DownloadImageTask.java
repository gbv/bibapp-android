package de.eww.bibapp.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import de.eww.bibapp.model.ModsItem;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
	ImageView imageView;
	ModsItem item;
	Context context;
	
	public DownloadImageTask(ImageView imageView, ModsItem item, Context context)
	{
		this.imageView = imageView;
		this.item = item;
		this.context = context;
	}
	
	@Override
	protected Bitmap doInBackground(String... urls)
	{
		String urlParam = urls[0];
		Bitmap bitmap = null;
		
		try
		{
			Log.v("BITMAP", urlParam);
			URL url = new URL(urlParam);
			InputStream input = url.openStream();
			bitmap = BitmapFactory.decodeStream(input);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap result)
	{
		// if no image is found, set the list image
		if ( result == null )
		{
			this.setListImage();
		}
		else
		{
			// even if the result is not null, we can not ensure that this is a valid bitmap
			// maybe checking the byte count should help
			//int byteCount = result.getByteCount();
            int byteCount = result.getRowBytes() * result.getHeight();
			
			if ( byteCount > 1 )
			{
				this.imageView.setImageBitmap(result);
			}
			else
			{
				this.setListImage();
			}
		}
	}
	
	private void setListImage()
	{
		Resources res = this.context.getResources();
		this.imageView.setImageResource(res.getIdentifier("mediaicon_" + this.item.mediaType.toLowerCase(Locale.GERMANY), "drawable", this.context.getPackageName()));
	}
}
