package de.eww.bibapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DaiaItem;

public class AvailableAdapterOld extends ArrayAdapter<DaiaItem>
{
	private final Context context;
	private boolean isLocalSearch = true;
	
	public AvailableAdapterOld(Context context, int textViewResourceId)
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
		
		DaiaItem entry = this.getItem(position);
		
		// prepare department text
		String departmentText = "";
		
		if (!Constants.EXEMPLAR_SHORT_DISPLAY) {
			if (entry.department != null && !entry.department.isEmpty()) {
				departmentText = entry.department;
			}
			
			if (entry.storage != null && !entry.storage.isEmpty()) {
				if (!departmentText.isEmpty()) {
					departmentText += ", ";
				}
				
				departmentText += entry.storage;
			}
		} else {
			if (entry.storage != null && !entry.storage.isEmpty()) {
				departmentText = entry.storage;
			} else if(entry.department != null && !entry.department.isEmpty()) {
				departmentText = entry.department;
			}
		}
		
		if (!departmentText.isEmpty()) {
			departmentView.setText(departmentText);
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
