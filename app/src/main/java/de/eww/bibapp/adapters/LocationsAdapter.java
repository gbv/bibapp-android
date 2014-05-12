package de.eww.bibapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.eww.bibapp.R;
import de.eww.bibapp.data.LocationsEntry;

public class LocationsAdapter extends ArrayAdapter<LocationsEntry>
{
	private final Context context;
	
	public LocationsAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_locations_item_view, parent, false);
		
		TextView titleView = (TextView) v.findViewById(R.id.locations_item_title);
		
		LocationsEntry entry = this.getItem(position);
		
		titleView.setText(entry.listName);
		
		return v;
	}
}
