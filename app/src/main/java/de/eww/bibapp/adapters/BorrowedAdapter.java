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
import de.eww.bibapp.data.PaiaDocument;

public class BorrowedAdapter extends ArrayAdapter<PaiaDocument>
{
	private final Context context;
    private final boolean isRequestPermitted;
	
	public BorrowedAdapter(Context context, int textViewResourceId, boolean isRequestPermitted)
	{
		super(context, textViewResourceId);
		
		this.context = context;
        this.isRequestPermitted = isRequestPermitted;
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

        PaiaDocument document = this.getItem(position);
		
		aboutView.setText(document.getAbout());
		signatureView.setText(document.getLabel());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
		
		if ( document.getDueDate() != null )
		{
			dateView.setText(dateFormat.format(document.getDueDate()));
		}
		
		queueView.setText(String.valueOf(document.getQueue()));
		renewalView.setText(String.valueOf(document.getRenewals()));
		statusView.setText(document.getStorage());

        if (document.isCanRenew() && this.isRequestPermitted) {
            checkBox.setVisibility(View.VISIBLE);
        }
		
		return v;
	}
}
