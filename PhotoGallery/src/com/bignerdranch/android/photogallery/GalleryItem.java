package com.bignerdranch.android.photogallery;

public class GalleryItem {
	
	private String mCaption;
	private String mID;
	private String mURL;
	
	public GalleryItem(String id, String caption, String url) {
		mID = id;
		mCaption = caption;
		mURL = url;
	}

	public String getCaption() {
		return mCaption;
	}

	public void setCaption(String caption) {
		mCaption = caption;
	}

	public String getID() {
		return mID;
	}

	public void setID(String iD) {
		mID = iD;
	}

	public String getURL() {
		return mURL;
	}

	public void setURL(String uRL) {
		mURL = uRL;
	}
	
	@Override
	public String toString() {
		return mCaption;
	}

}
