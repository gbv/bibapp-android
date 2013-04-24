package de.eww.bibapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.eww.bibapp.R;
import de.eww.bibapp.data.AvailableEntry;

public class AvailableAdapter extends ArrayAdapter<AvailableEntry>
{
	private final Context context;
	private boolean isLocalSearch = true;
	
	public AvailableAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
	}
	
	public void setIsLocalSearch(boolean isLocalSearch)
	{
		this.isLocalSearch = isLocalSearch;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_detail_available_item_view, parent, false);
		
		// set daia information
		TextView distanceView = (TextView) v.findViewById(R.id.detail_item_daia_distance);
		TextView departmentView = (TextView) v.findViewById(R.id.detail_item_daia_department);
		TextView labelView = (TextView) v.findViewById(R.id.detail_item_daia_label);
		TextView statusView = (TextView) v.findViewById(R.id.detail_item_daia_status);
		TextView statusInfoView = (TextView) v.findViewById(R.id.detail_item_daia_status_info);
		
		AvailableEntry entry = this.getItem(position);
		
		if ( !entry.storage.isEmpty() )
		{
			departmentView.setText(entry.storage);
		}
		else
		{
			departmentView.setText(entry.department);
		}
		
		labelView.setText(entry.label);
		
		if ( this.isLocalSearch )
		{
			statusView.setText(entry.status);
			statusView.setTextColor(Color.parseColor(entry.statusColor));
			statusView.setVisibility(View.VISIBLE);
			statusInfoView.setText(entry.statusInfo);
			statusInfoView.setVisibility(View.VISIBLE);
		}
		else
		{
			statusView.setText("");
			statusInfoView.setText("");
		}
		
		if ( entry.distance != null )
		{
			distanceView.setText(String.format("%.2f km", entry.distance));
			distanceView.setVisibility(View.VISIBLE);
		}
		
		return v;
	}
}
