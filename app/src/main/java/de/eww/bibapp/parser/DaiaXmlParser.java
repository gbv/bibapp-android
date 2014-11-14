package de.eww.bibapp.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.eww.bibapp.model.DaiaItem;
import de.eww.bibapp.model.ModsItem;

public class DaiaXmlParser
{
	// We don't use namespaces
    private static final String ns = null;
    
    private ModsItem item;
    
    public DaiaXmlParser(ModsItem item)
    {
    	this.item = item;
    }
    
	public ArrayList<DaiaItem> parse(InputStream in) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			
			return this.readDaia(parser);
		}
		finally
		{
			in.close();
		}
	}
	
	private ArrayList<DaiaItem> readDaia(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		ArrayList<DaiaItem> entryList = new ArrayList<DaiaItem>();
		
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "daia");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("document") )
			{
				entryList = this.readDocument(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entryList;
	}
	
	private ArrayList<DaiaItem> readDocument(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		ArrayList<DaiaItem> itemList = new ArrayList<DaiaItem>();
		String itemUriUrl = "";
		
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "document");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("item") )
			{
				HashMap<String, String> itemAttributes = this.readAttributes(parser);
				if ( itemAttributes.containsKey("id") )
				{
					itemUriUrl = itemAttributes.get("id");
				}
				DaiaItem item = this.readItem(parser);
				item.setItemUriUrl(itemUriUrl);
				itemList.add(item);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return itemList;
	}
	
	private DaiaItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "item");
		
		String label = "";
		String uriUrl = "";
		String limitation = "";
		String storage = "";
		
		HashMap<String, HashMap<String, String>> availableItems = new HashMap<String, HashMap<String, String>>();
		HashMap<String, HashMap<String, String>> unavailableItems = new HashMap<String, HashMap<String, String>>();
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("label") )
			{
				label = this.readLabel(parser);
			}
			else if ( name.equals("available") )
			{
				HashMap<String, String> availableAttributes = this.readAttributes(parser);
				
				availableItems.put(availableAttributes.get("service"), availableAttributes);
				String readLimitation = this.readAvailable(parser);
				
				// read limitation only from the "presentation" attribute
				if (availableAttributes.get("service").equals("presentation")) {
					if ( !readLimitation.isEmpty() )
					{
						limitation = readLimitation;
					}
				}
			}
			else if ( name.equals("unavailable") )
			{
				HashMap<String, String> unavailableAttributes = this.readAttributes(parser);
				
				unavailableItems.put(unavailableAttributes.get("service"), unavailableAttributes);
				String readLimitation = this.readUnavailable(parser);
				
				// read limitation only from the "presentation" attribute
				if (unavailableAttributes.get("service").equals("presentation")) {
					if ( !readLimitation.isEmpty() )
					{
						limitation = readLimitation;
					}
				}
			}
			else if ( name.equals("storage") )
			{
				storage = this.readStorage(parser);
			}
			else if ( name.equals("department") )
			{
				HashMap<String, String> departmentAttributes = this.readAttributes(parser);
				
				if ( departmentAttributes.containsKey("id") )
				{
					uriUrl = departmentAttributes.get("id");
				}
				
				this.skip(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		String status = "";
		String statusColor = "#000000";
		String statusInfo = "";
		
		if ( availableItems.containsKey("loan") )
		{
			status += "ausleihbar";
			statusColor = "#007F00";
			
			if ( (availableItems.containsKey("presentation") || availableItems.containsKey("presentation") ) && !limitation.isEmpty() )
			{
				status += "; " + limitation;
			}
			
			if ( availableItems.containsKey("presentation") )
			{
				// tag available with service="loan" and href=""
				if ( availableItems.get("loan").containsKey("href") )
				{
					statusInfo += "Bitte bestellen";
				}
				else
				{
					statusInfo += "Bitte am Standort entnehmen";
				}
			}
		}
		else
		{
			if ( unavailableItems.containsKey("loan") && unavailableItems.get("loan").containsKey("href") )
			{
				if ( unavailableItems.get("loan").get("href").contains("loan/RES") )
				{
					status += "ausleihbar";
					statusColor = "#FF7F00";
				}
				else
				{
					status += "nicht ausleihbar";
					statusColor = "#FF0000";
				}
			}
			else
			{
				// if this is not a online resource
				if ( item.onlineUrl.isEmpty() )
				{
					status += "nicht ausleihbar";
					statusColor = "#FF0000";
				}
				else
				{
					status += "Online-Ressource im Browser öffnen";
				}
			}
			
			if ( ( availableItems.containsKey("presentation") || unavailableItems.containsKey("presentation") ) && !limitation.isEmpty() )
			{
				status += "; " + limitation;
			}
			
			if ( unavailableItems.containsKey("presentation") )
			{
				if ( unavailableItems.get("loan").containsKey("href") )
				{
					if ( unavailableItems.get("loan").get("href").contains("loan/RES") )
					{
						if ( !unavailableItems.get("loan").containsKey("expected") || unavailableItems.get("loan").get("expected").equals("unknown") )
						{
							statusInfo += "ausgeliehen, Vormerken möglich";
						}
						else
						{
							String dateString = unavailableItems.get("loan").get("expected");
							SimpleDateFormat simpleDateFormat;
							
							if ( dateString.substring(2, 3).equals("-") && dateString.substring(5, 6).equals("-") )
							{
								simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.GERMANY);
							}
							else
							{
								simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
							}
							
							try
							{
								Date date = simpleDateFormat.parse(dateString);
								
								SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
								statusInfo += "ausgeliehen bis " + dateFormat.format(date) + ", Vormerken möglich";
							}
							catch (ParseException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				else
				{
					statusInfo += "...";
				}
			}
		}
		
		String actions = "";
		if ( availableItems.containsKey("loan") )
		{
			if ( availableItems.containsKey("presentation") )
			{
				if ( availableItems.get("loan").containsKey("href") )
				{
					actions = "order";
				}
			}
		}
		else
		{
			if ( unavailableItems.containsKey("presentation") )
			{
				if ( unavailableItems.get("loan").containsKey("href") )
				{
					actions = "request";
				}
			}
		}
		
		if (
				(
					availableItems.containsKey("loan") &&
					availableItems.get("loan").containsKey("href") &&
					!availableItems.get("loan").get("href").isEmpty()
				) ||
				(
					unavailableItems.containsKey("loan") &&
					unavailableItems.get("loan").containsKey("href") &&
					!unavailableItems.get("loan").get("href").isEmpty()
				)
		)
		{
			actions += ";location";
		}
		else
		{
			// fix for crash when tryining to access a location entry that does not exists
			// the default actions depend on the existence of a uri entry
			if (!uriUrl.isEmpty()) {
				actions = "location";
			}
		}
		
		return new DaiaItem(label, uriUrl, status, statusColor, statusInfo, actions, storage);
	}
	
	private String readLabel(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "label");
	    String label = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, DaiaXmlParser.ns, "label");
	    return label;
	}
	
	private String readStorage(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "storage");
	    String storage = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, DaiaXmlParser.ns, "storage");
	    return storage;
	}
	
	private HashMap<String, String> readAttributes(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		HashMap<String, String> attributes = null;
		
		int numAttributes = parser.getAttributeCount();
		if ( numAttributes != -1 )
		{
			attributes = new HashMap<String, String>(numAttributes);
			
			for ( int i=0; i < numAttributes; i++ )
			{
				attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
			}
		}
		
		return attributes;
	}
	
	private String readAvailable(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "available");
		String limitation = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("limitation") )
			{
				limitation = this.readLimitation(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return limitation;
	}
	
	private String readUnavailable(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "unavailable");
		String limitation = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("limitation") )
			{
				limitation = this.readLimitation(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return limitation;
	}
	
	private String readLimitation(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, DaiaXmlParser.ns, "limitation");
	    String limitation = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, DaiaXmlParser.ns, "limitation");
	    
	    return limitation;
	}
	
	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String result = "";
		
		if ( parser.next() == XmlPullParser.TEXT )
		{
			result = parser.getText();
			parser.nextTag();
		}
		
		return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if ( parser.getEventType() != XmlPullParser.START_TAG )
		{
			throw new IllegalStateException();
		}
		
		int depth = 1;
		while ( depth != 0)
		{
			switch ( parser.next() )
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}