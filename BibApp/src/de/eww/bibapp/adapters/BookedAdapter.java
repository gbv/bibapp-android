package de.eww.bibapp.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.eww.bibapp.R;
import de.eww.bibapp.data.BookedEntry;

public class BookedAdapter extends ArrayAdapter<BookedEntry>
{
	private final Context context;
	
	public BookedAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_booked_item_view, parent, false);
		
		TextView aboutView = (TextView) v.findViewById(R.id.booked_item_about);
		TextView signatureView = (TextView) v.findViewById(R.id.booked_item_signature);
		TextView dateView = (TextView) v.findViewById(R.id.booked_item_date);
		
		BookedEntry entry = this.getItem(position);
		
		aboutView.setText(entry.about);
		signatureView.setText(entry.signature);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
		dateView.setText(dateFormat.format(entry.date));
		
		return v;
	}
}
