package com.bignerdranch.android.criminalintent;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_DATE = "date";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_SUSPECT = "suspect";
	private static final String JSON_SUSPECT_ID = "suspectID";
	
	private UUID mID;
	private String mTitle;
	private Calendar mDate;
	private boolean mSolved;
	private String mSuspect;
	private String mSuspectID;
	
	public Crime() {
		// Generate unique identifier
		mID = UUID.randomUUID();
		mDate = Calendar.getInstance();
	}
	
	public Crime(JSONObject json) throws JSONException {
		mID = UUID.fromString(json.getString(JSON_ID));
		if(json.has(JSON_TITLE)){
			mTitle = json.getString(JSON_TITLE);
		}
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = Calendar.getInstance();
		mDate.setTime(new Date(json.getLong(JSON_DATE)));
		if(json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
			mSuspectID = json.getString(JSON_SUSPECT_ID);
		}
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getID() {
		return mID;
	}

	public Calendar getDate() {
		return mDate;
	}
	
	public String getFormattedDate() {
		return DateFormat.getDateInstance().format(mDate.getTime());
	}
	
	public String getFormattedTime() {
		return DateFormat.getTimeInstance(DateFormat.SHORT).format(mDate.getTime());
	}
	
	public String getFormattedDateTime() {
		return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
				.format(mDate.getTime());
	}

	public void setDate(Calendar date) {
		mDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
		mDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
		mDate.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
	}
	
	public void setTime(Calendar time) {
		mDate.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		mDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	
	public String getSuspect() {
		return mSuspect;
	}
	
	public String getSuspectID() {
		return mSuspectID;
	}
	
	public void setSuspect(String suspect, String suspectID) {
		mSuspect = suspect;
		mSuspectID = suspectID;
	}
	
	@Override
	public String toString() {
		return mTitle;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mID.toString())
			.put(JSON_TITLE, mTitle)
			.put(JSON_DATE, mDate.getTime().getTime()) //get Date object, then long 
			.put(JSON_SOLVED, mSolved)
			.put(JSON_SUSPECT, mSuspect)
			.put(JSON_SUSPECT_ID, mSuspectID);
		return json;
	}
	
	@Override 
	public boolean equals(Object other) {
		if(other instanceof Crime) {
			Crime o = (Crime) other;
			return this.getID().equals(o.getID());
		}
		return false;
	}
}
