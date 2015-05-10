package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity 
	implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
	
	private static final int REQUEST_NEW_CRIME = 0;
	
	@Override //from SingleFragmentActivity
	protected int getLayoutResID() {
		return R.layout.activity_masterDetail;
	}

	@Override //from Activity
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override //from CrimeListFragment.Callbacks
	public void onCrimeSelected(Crime crime) {
		if(findViewById(R.id.detailFragmentContainer) == null) { //phone
			Intent i = new Intent(this, CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());
			startActivity(i);
		}
		else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldFragment = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = CrimeFragment.newInstance(crime.getID());
			
			if(oldFragment != null) {
				ft.remove(oldFragment);
			}
			
			ft.add(R.id.detailFragmentContainer, newDetail);
			ft.commit();
		}
	}

	@Override //from CrimeListFragment.Callbacks
	public void onNewCrime(Crime crime) {
		Intent i = new Intent(this, CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());
		i.putExtra(CrimeFragment.EXTRA_IS_NEW_CRIME, true);
		startActivityForResult(i, REQUEST_NEW_CRIME);		
	}
	
	@Override //from Activity
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_NEW_CRIME) {
			if(resultCode == Activity.RESULT_CANCELED) {
				UUID crimeID = (UUID) data.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
				CrimeLab.get(this).deleteCrime(crimeID);
			}
		}
	}

	@Override //from CrimeFragment.Callbacks
	public void onCrimeDeleted(Crime crime) {
		CrimeLab.get(this).deleteCrime(crime.getID());
		FragmentManager fm = getSupportFragmentManager();
		CrimeFragment detailFragment = (CrimeFragment) fm.findFragmentById(R.id.detailFragmentContainer);
		fm.beginTransaction()
			.remove(detailFragment)
			.commit();
		
		CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
		
		listFragment.updateUI();
	}

	@Override //from CrimeFragment.Callbacks
	public void onCrimeUpdated(Crime crime) {
		CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentContainer);
		
		listFragment.updateUI();		
	}

}
