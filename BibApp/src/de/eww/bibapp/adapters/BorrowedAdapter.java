package de.eww.bibapp.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import de.eww.bibapp.R;
import de.eww.bibapp.data.BorrowedEntry;

public class BorrowedAdapter extends ArrayAdapter<BorrowedEntry>
{
	private final Context context;
	
	public BorrowedAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_borrowed_item_view, parent, false);
		
		TextView aboutView = (TextView) v.findViewById(R.id.borrowed_item_about);
		TextView signatureView = (TextView) v.findViewById(R.id.borrowed_item_signature);
		TextView dateView = (TextView) v.findViewById(R.id.borrowed_item_duedate);
		TextView queueView = (TextView) v.findViewById(R.id.borrowed_item_queue);
		TextView renewalView = (TextView) v.findViewById(R.id.borrowed_item_renewals);
		TextView statusView = (TextView) v.findViewById(R.id.borrowed_item_status);
		CheckBox checkBox = (CheckBox) v.findViewById(R.id.borrowed_item_checkbox);
		
		BorrowedEntry entry = this.getItem(position);
		
		aboutView.setText(entry.about);
		signatureView.setText(entry.signature);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
		
		if ( entry.duedate != null )
		{
			dateView.setText(dateFormat.format(entry.duedate));
		}
		
		queueView.setText(String.valueOf(entry.queue));
		renewalView.setText(String.valueOf(entry.renewals));
		statusView.setText(entry.status);
		
		if ( entry.canRenew == false )
		{
			checkBox.setEnabled(false);
		}
		
		return v;
	}
}
