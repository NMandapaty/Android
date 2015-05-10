package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity 
	implements CrimeFragment.Callbacks {
	
	private static final String TAG = "CrimePagerActivity";
	
	private ViewPager mViewPager;
	private CrimeLab mCrimeLab;
	private int mStartingPos;
	private boolean mStartingCrimeIsNew;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Create view (instead of using XML)
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.view_pager);
		setContentView(mViewPager);
		
		mCrimeLab = CrimeLab.get(this);
		ArrayList<Crime> crimes = mCrimeLab.getCrimes();
		
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return mCrimeLab.getNumCrimes();
			}
			
			@Override
			public Fragment getItem(int pos) {
				Crime c = mCrimeLab.getCrimes().get(pos);
				if(pos == mStartingPos) {
					return CrimeFragment.newInstance(c.getID(), mStartingCrimeIsNew, c.getID());
				}
				return CrimeFragment.newInstance(c.getID());
			}
		});
		
		//Find crime clicked
		UUID crimeID = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		mStartingCrimeIsNew = getIntent().getBooleanExtra(CrimeFragment.EXTRA_IS_NEW_CRIME, false);
		int i = 0; 
		Crime c = crimes.get(i);
		while(! c.getID().equals(crimeID)) {
			i++;
			c = crimes.get(i);
		}
		mStartingPos = i;
		mViewPager.setCurrentItem(i);
		setTitle(c.getTitle());
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				Crime crime = mCrimeLab.getCrimes().get(pos);
				if(crime.getTitle() != null) {
					setTitle(crime.getTitle());
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	@Override
	public void onCrimeDeleted(Crime crime) {
		mCrimeLab.deleteCrime(crime);
		this.finish();		
	}

	@Override
	public void onCrimeUpdated(Crime crime) {
		// Does nothing		
	}

}
