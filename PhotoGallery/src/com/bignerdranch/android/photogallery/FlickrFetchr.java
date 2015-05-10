package com.bignerdranch.android.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

public class FlickrFetchr {
	
	public static final String PREF_SEARCH_QUERY = "search query";
	public static final String PREF_LAST_RESULT_ID = "last result id";
	
	private static final String TAG = "FlickrFetchr";
	
	private static final String ENDPOINT = "https://api.flickr.com/services/rest";
	private static final String PARAM_KEY = "api_key";
	private static final String KEY = "c6c211185c2405da4ef5cc2b34187687";
	private static final String PARAM_METHOD = "method";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String METHOD_SEARCH = "flickr.photos.search";
	private static final String PARAM_EXTRAS = "extras";
	private static final String PARAM_TEXT = "text";
	private static final String EXTRA_SMALL_URL = "url_s";
	
	private static final String XML_PHOTO = "photo";
	
	byte[] getURLBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			
			out.close();
			return out.toByteArray();
		}
		finally {
			connection.disconnect();
		}
	}
	
	public String getURL(String urlSpec) throws IOException {
		return new String(getURLBytes(urlSpec));
	}
	
	public ArrayList<GalleryItem> downloadGalleryItems(String url) {
		ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
		try {
			String xmlString = getURL(url);
			Log.i(TAG, "Received XML: " + xmlString);
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			
			parseItems(items, parser);
		} catch(IOException ioe) {
			Log.e(TAG, "Failed to receive XML", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to parse items", xppe);
		}
		
		return items;
	}
	
	public ArrayList<GalleryItem> fetchItems() {
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter(PARAM_METHOD, METHOD_GET_RECENT)
				.appendQueryParameter(PARAM_KEY, KEY)
				.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
				.build().toString();
		
		return downloadGalleryItems(url);
	}
	
	public ArrayList<GalleryItem> search(String query) {
		String url = Uri.parse(ENDPOINT).buildUpon()
					.appendQueryParameter(PARAM_METHOD, METHOD_SEARCH)
					.appendQueryParameter(PARAM_KEY, KEY)
					.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
					.appendQueryParameter(PARAM_TEXT, query)
					.build().toString();
		
		return downloadGalleryItems(url);
	}
	
	void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG && parser.getName().equals(XML_PHOTO)) {
				String id = parser.getAttributeValue(null, "id");
				String caption = parser.getAttributeValue(null, "title");
				String smallURL = parser.getAttributeValue(null, EXTRA_SMALL_URL);
				
				GalleryItem item = new GalleryItem(id, caption, smallURL);
				items.add(item);
			}
			
			eventType = parser.next();
		}
	}
	
	public static String base_encode(long num, String digits) {
		int base = digits.length();
		String encoded = "";
		while(num >= base) {
			int mod = (int) (num % base);
			encoded = digits.substring(mod, mod+1) + encoded;
			num = num/base;
		}
		
		if(num > 0) { //last digit
			encoded = digits.substring((int) num, (int) num+1) + encoded;
		}
		
		return encoded;
	}

}
