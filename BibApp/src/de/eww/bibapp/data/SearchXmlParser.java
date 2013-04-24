package de.eww.bibapp.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class SearchXmlParser
{
	// We don't use namespaces
    private static final String ns = null;
    
	public HashMap<String, Object> parse(InputStream in, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			
			return this.readFeed(parser, isLocalSearch);
		}
		finally
		{
			in.close();
		}
	}
	
	private HashMap<String, Object> readFeed(XmlPullParser parser, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<SearchEntry> list = new ArrayList<SearchEntry>();
		map.put("numberOfRecords", 0);
		map.put("list", list);
		
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "zs:searchRetrieveResponse");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("zs:records") )
			{
				map.put("list", this.readRecords(parser, isLocalSearch));
			}
			else if ( name.equals("zs:numberOfRecords") )
			{
				map.put("numberOfRecords", this.readNumberOfRecords(parser));
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return map;
	}
	
	private List<SearchEntry> readRecords(XmlPullParser parser, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		List<SearchEntry> entries = new ArrayList<SearchEntry>();
		
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "zs:records");
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("zs:record") )
			{
				entries.add(this.readRecord(parser, isLocalSearch));
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entries;
	}
	
	private SearchEntry readRecord(XmlPullParser parser, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		SearchEntry entry = null;
		
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "zs:record");
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("zs:recordData") )
			{
				entry = this.readRecordData(parser, isLocalSearch);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entry;
	}
	
	private SearchEntry readRecordData(XmlPullParser parser, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		SearchEntry entry = null;
		
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "zs:recordData");
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("mods") )
			{
				entry = this.readMods(parser, isLocalSearch);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entry;
	}
	
	private SearchEntry readMods(XmlPullParser parser, boolean isLocalSearch) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "mods");
		
		HashMap<String, String> titleInfoMap = null;
		ArrayList<String> authors = new ArrayList<String>();
		String typeOfResource = null;
		String physicalDescription = null;
		boolean isEssay = false;
		ArrayList<String> indexArray = new ArrayList<String>();
		String originInfo = null;
		String ppn = null;
		String isbn = "";
		String onlineUrl = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("titleInfo") )
			{
				if ( titleInfoMap == null )
				{
					titleInfoMap = this.readTitleInfo(parser);
				}
				else
				{
					this.skip(parser);
				}
			}
			else if ( name.equals("physicalDescription") )
			{
				physicalDescription = this.readPhysicalDescription(parser);
			}
			else if ( name.equals("typeOfResource") )
			{
				typeOfResource = this.readTypeOfResource(parser);
			}
			else if ( name.equals("relatedItem") )
			{
				HashMap<String, Object> relatedItem = this.readRelatedItem(parser);
				
				if ( relatedItem.containsKey("isEssay") && isEssay == false )
				{
					if ( ((Boolean) relatedItem.get("isEssay")).booleanValue() == true )
					{
						isEssay = true;
					}
				}
				
				if ( relatedItem.containsKey("index") )
				{
					String indexString = (String) relatedItem.get("index");
					
					if ( !indexString.isEmpty() )
					{
						indexArray.add(indexString);
					}
				}
			}
			else if ( name.equals("originInfo") )
			{
				originInfo = this.readOriginInfo(parser);
			}
			else if ( name.equals("identifier") )
			{
				HashMap<String, String> attributes = this.readAttributes(parser);
				if ( attributes.containsKey("type") && attributes.get("type").equals("isbn") && isbn.isEmpty() )
				{
					String readIdentifer = this.readIdentifier(parser);
					
					Pattern pattern = Pattern.compile("([0-9]+)", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(readIdentifer);
					if ( matcher.find() )
					{
						isbn = matcher.group(1);
					}
				}
				else
				{
					this.skip(parser);
				}
			}
			else if ( name.equals("recordInfo") )
			{
				ppn = this.readRecordInfo(parser);
			}
			else if ( name.equals("location") )
			{
				String tagUrl = this.readLocation(parser);
				if ( !tagUrl.isEmpty() )
				{
					onlineUrl = tagUrl;
				}
			}
			else if ( name.equals("name") )
			{
				String authorName = this.readName(parser);
				if ( !authorName.isEmpty() )
				{
					authors.add(authorName);
				}
			}
			else
			{
				this.skip(parser);
			}
		}
		
		// determ media type
		String mediaType = "X";
		
		if ( physicalDescription != null && physicalDescription.equals("microform") )
		{
			mediaType = "E";
		}
		else if ( typeOfResource != null && typeOfResource.equals("manuscript") )
		{
			mediaType = "H";
		}
		else if ( isEssay == true )
		{
			mediaType = "A";
		}
		else
		{
			if ( typeOfResource != null )
			{
				if ( typeOfResource.equals("still image") )
				{
					mediaType = "I";
				}
				else if ( typeOfResource.equals("sound recording-musical") )
				{
					mediaType = "G";
				}
				else if ( typeOfResource.equals("sound recording-nonmusical") )
				{
					mediaType = "G";
				}
				else if ( typeOfResource.equals("sound recording") )
				{
					mediaType = "G";
				}
				else if ( typeOfResource.equals("cartographic") )
				{
					mediaType = "K";
				}
				else if ( typeOfResource.equals("notated music") )
				{
					mediaType = "M";
				}
				else if ( typeOfResource.equals("moving image") )
				{
					mediaType = "V";
				}
				else if ( typeOfResource.equals("text") )
				{
					if ( originInfo != null && ( originInfo.equals("serial") || originInfo.equals("continuing") ) )
					{
						mediaType = "T";
					}
					else
					{
						mediaType = "B";
					}
				}
				else if ( typeOfResource.equals("software, multimedia") )
				{
					if ( originInfo != null && ( originInfo.equals("serial") || originInfo.equals("continuing") ) )
					{
						if ( physicalDescription != null && physicalDescription.equals("remote") )
						{
							mediaType = "P";
						}
						else
						{
							mediaType = "T";
						}
					}
					else
					{
						if ( physicalDescription != null && physicalDescription.equals("remote") )
						{
							mediaType = "O";
						}
						else
						{
							mediaType = "S";
						}
					}
				}
			}
		}
		
		SearchEntry entry = new SearchEntry((String) titleInfoMap.get("title"), (String) titleInfoMap.get("subTitle"), (String) titleInfoMap.get("partNumber"), (String) titleInfoMap.get("partName"), mediaType, ppn, isbn, authors, onlineUrl, indexArray);
		entry.setIsLocalSearch(isLocalSearch);
		
		return entry;
	}
	
	private String readOriginInfo(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "originInfo");
		String originInfo = null;
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("issuance") )
			{
				originInfo = this.readIssuance(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return originInfo;
	}
	
	private String readName(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "name");
		String authorName = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("namePart") )
			{
				HashMap<String, String> attributes = this.readAttributes(parser);
				
				if ( !attributes.isEmpty() )
				{
					if ( attributes.get("type").equals("family") )
					{
						authorName += " " + this.readNamePart(parser);
					}
					else if ( attributes.get("type").equals("given") )
					{
						authorName = this.readNamePart(parser) + authorName;
					}
					else
					{
						this.skip(parser);
					}
				}
				else
				{
					this.skip(parser);
				}
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return authorName;
	}
	
	private String readRecordInfo(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "recordInfo");
		String recordInfo = null;
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("recordIdentifier") )
			{
				recordInfo = this.readRecordIdentifier(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return recordInfo;
	}
	
	private int readNumberOfRecords(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "zs:numberOfRecords");
	    int number = Integer.valueOf(this.readText(parser));
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "zs:numberOfRecords");
	    return number;
	}
	
	private String readIssuance(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "issuance");
	    String issuance = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "issuance");
	    return issuance;
	}
	
	private String readNamePart(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "namePart");
	    String namePart = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "namePart");
	    return namePart;
	}
	
	private String readRecordIdentifier(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "recordIdentifier");
	    String recordIdentifier = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "recordIdentifier");
	    
	    if ( recordIdentifier.contains("CIANDO") )
	    {
	    	recordIdentifier = recordIdentifier.replace("CIANDO", "");
	    }
	    
	    return recordIdentifier;
	}
	
	private String readLocation(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "location");
		String onlineUrl = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("url") )
			{
				onlineUrl = this.readUrl(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return onlineUrl;
	}
	
	private String readUrl(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "url");
		String onlineUrl = "";
		
		HashMap<String, String> attributes = this.readAttributes(parser);
		if ( attributes.containsKey("usage") && attributes.get("usage").equals("primary display") )
		{
			onlineUrl = this.readText(parser);
		}
		else
		{
			this.skip(parser);
		}
		
		return onlineUrl;
	}
	
	private HashMap<String, String> readTitleInfo(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "titleInfo");
		String title = null;
		String nonSort = null;
		String subTitle = "";
		String partNumber = "";
		String partName = "";
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("title") )
			{
				title = this.readTitle(parser);
			}
			else if ( name.equals("nonSort") )
			{
				nonSort = this.readNonSort(parser);
			}
			else if ( name.equals("subTitle") )
			{
				subTitle = this.readSubTitle(parser);
			}
			else if ( name.equals("partNumber") )
			{
				partNumber = this.readPartNumber(parser);
			}
			else if ( name.equals("partName") )
			{
				partName = this.readPartName(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		if ( nonSort != null )
		{
			title = nonSort + title;
		}
		map.put("title", title);
		
		map.put("subTitle", subTitle);
		map.put("partNumber", partNumber);
		map.put("partName", partName);
		
		return map;
	}
	
	private HashMap<String, Object> readRelatedItem(XmlPullParser parser ) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "relatedItem");
		HashMap<String, String> attributes = this.readAttributes(parser);
		
		boolean isEssay = false;
		String index = "";
		HashMap<String, Object> relatedItemContent = new HashMap<String, Object>();
		
		if ( attributes.containsKey("type") && attributes.get("type").equals("host") )
		{
			if ( attributes.containsKey("displayLabel") && attributes.get("displayLabel").contains("In: ") )
			{
				isEssay = true;
			}
		}
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("location") )
			{
				String readIndex = this.readRelatedItemLocation(parser);
				if ( !readIndex.isEmpty() )
				{
					index = readIndex;
				}
			}
			else
			{
				this.skip(parser);
			}
		}
		
		relatedItemContent.put("isEssay", isEssay);
		relatedItemContent.put("index", index);
		
		return relatedItemContent;
	}
	
	private String readRelatedItemLocation(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "location");
		String index = "";
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("url") )
			{
				index = this.readRelatedItemUrl(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return index;
	}
	
	private String readRelatedItemUrl(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "url");
	    String url = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "url");
	    return url;
	}
	
	private String readIdentifier(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "identifier");
	    String identifier = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "identifier");
	    return identifier;
	}
	
	private String readTypeOfResource(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "typeOfResource");
		HashMap<String, String> attributes = this.readAttributes(parser);
		
		String typeOfResource = null;
		
		if ( attributes.containsKey("manuscript") && attributes.get("manuscript").equals("yes") )
		{
			typeOfResource = "manuscript";
			this.readText(parser);
		}
		else
		{
			typeOfResource = this.readText(parser);
		}
		
		parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "typeOfResource");
		
		return typeOfResource;
	}
	
	private String readPhysicalDescription(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "physicalDescription");
		String mediatype = null;
		
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			if ( name.equals("form") && mediatype == null )
			{
				// read attributes
				HashMap<String, String> attributes = this.readAttributes(parser);
				
				Iterator<Entry<String, String>> it = attributes.entrySet().iterator();
				if ( it.hasNext() )
				{
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
					
					if ( pair.getKey().equals("authority") && pair.getValue().contains("marc") )
					{
						String form = this.readForm(parser);
						
						if ( form.equals("microform") || form.equals("remote") )
						{
							mediatype = form;
						}
					}
					else
					{
						this.skip(parser);
					}
					
					it.remove();
				}
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return mediatype;
	}
	
	private String readForm(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "form");
	    String form = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "form");
	    return form;
	}
	
	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "title");
	    String title = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "title");
	    return title;
	}
	
	private String readNonSort(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "nonSort");
	    String nonSort = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "nonSort");
	    return nonSort;
	}
	
	private String readSubTitle(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "subTitle");
	    String subTitle = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "subTitle");
	    return subTitle;
	}
	
	private String readPartNumber(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "partNumber");
	    String partNumber = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "partNumber");
	    return partNumber;
	}
	
	private String readPartName(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, SearchXmlParser.ns, "partName");
	    String partName = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, SearchXmlParser.ns, "partName");
	    return partName;
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