package de.eww.bibapp.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
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

        SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

        if (document.getEndDate() != null) {
            Date endDate = document.getEndDate();
            String endDateString = dateFormatWithTime.format(endDate);

            if (endDateString.contains("00:00")) {
                endDateString = dateFormatWithoutTime.format(endDate);
            }

            dateView.setText(endDateString);
        } else if(document.getDueDate() != null) {
            Date dueDate = document.getDueDate();

            String dueDateString = dateFormatWithTime.format(dueDate);

            if (dueDateString.contains("00:00")) {
                dueDateString = dateFormatWithoutTime.format(dueDate);
            }

            dateView.setText(dueDateString);
        }
		
		queueView.setText(String.valueOf(document.getQueue()));
		renewalView.setText(String.valueOf(document.getRenewals()));

        int statusCode = document.getStatus();
        String[] statusTranslations = this.getContext().getResources().getStringArray(R.array.paia_service_status);
        statusView.setText(statusTranslations[statusCode]);

        if (document.isCanRenew() && this.isRequestPermitted) {
            checkBox.setVisibility(View.VISIBLE);
        }
		
		return v;
	}
}
