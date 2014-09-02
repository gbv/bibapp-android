package de.eww.bibapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.data.FeeEntry;

public class FeeAdapter extends ArrayAdapter<FeeEntry>
{
	private final Context context;
	
	public FeeAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_fee_item_view, parent, false);
		
		TextView amountView = (TextView) v.findViewById(R.id.fee_item_amount);
		TextView aboutView = (TextView) v.findViewById(R.id.fee_item_about);
		TextView dateView = (TextView) v.findViewById(R.id.fee_item_date);
		
		FeeEntry entry = this.getItem(position);
		
		amountView.setText(entry.amount);
		aboutView.setText(entry.about);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
		dateView.setText(dateFormat.format(entry.date));
		
		return v;
	}
}
