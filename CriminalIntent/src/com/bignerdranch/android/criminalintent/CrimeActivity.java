package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.support.v4.app.Fragment;
import android.util.Log;

public class CrimeActivity extends SingleFragmentActivity {
	
	private static final String TAG = "CrimeActivity";

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
//		return new CrimeFragment();
		UUID crimeID = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeID);
	}
	
	

}
