package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment();
	
	protected int getLayoutResID() {
		return R.layout.activity_fragment;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
		
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
		
		if(fragment == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.fragmentContainer, createFragment())
				.commit();
		}
	}
}
