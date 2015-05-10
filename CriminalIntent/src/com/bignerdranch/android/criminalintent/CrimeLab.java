package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;

public class CrimeLab {
	
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	private CriminalIntentJSONSerializer mSerializer;
	
	private ArrayList<Crime> mCrimes;
	
	private CrimeLab(Context appContext) {
		mAppContext = appContext;
//		mCrimes = new ArrayList<Crime>();
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
			mCrimes = mSerializer.loadCrimes();
		}
		catch(Exception e) {
			Log.d(TAG, "Error loading crimes: ", e);
			mCrimes = new ArrayList<Crime>();
		}
		
	}
	
	public static CrimeLab get(Context c) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		
		return sCrimeLab;
	}
	
	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}
	
	public void addCrime(Crime c) {
		mCrimes.add(c);
	}
	
	public void deleteCrime(Crime c) {
		mCrimes.remove(c);
	}
	
	public void deleteCrime(UUID id) {
		Crime c = getCrime(id);
		mCrimes.remove(c);
	}
	
	public int getNumCrimes() {
		return mCrimes.size();
	}
	
	public Crime getCrime(UUID id) {
		for (Crime c : mCrimes) {
			if (c.getID().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	public boolean saveCrimes() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "crimes saved to file");
			return true;
		}
		catch(Exception e) {
			Log.d(TAG, "error saving crimes: ", e);
			return false;
		}
	}
	
}
