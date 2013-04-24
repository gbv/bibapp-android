package de.eww.bibapp.data;

import java.util.Date;

public class BorrowedEntry
{
	public final String about;
	public final String	signature;
	public final Date duedate;
	public final int queue;
	public final int renewals;
	public final String status;
	public final String item;
	public final String edition;
	public final String barcode;
	public final boolean canRenew;
	
	public BorrowedEntry(String about, String signature, Date duedate, int queue, int renewals, String status, String item, String edition, String barcode, boolean canRenew)
	{
		this.about = about;
		this.signature = signature;
		this.duedate = duedate;
		this.queue = queue;
		this.renewals = renewals;
		this.status = status;
		this.item = item;
		this.edition = edition;
		this.barcode = barcode;
		this.canRenew = canRenew;
	}
}
