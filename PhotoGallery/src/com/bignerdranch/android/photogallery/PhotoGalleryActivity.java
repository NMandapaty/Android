package com.bignerdranch.android.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {
	
	private static final String TAG = "PhotoGalleryActivity";

	@Override
	protected Fragment createFragment() {
		return new PhotoGalleryFragment();
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentContainer);
		if(intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "Received a new search query: " + query);
			
			PreferenceManager.getDefaultSharedPreferences(this)
				.edit()
				.putString(FlickrFetchr.PREF_SEARCH_QUERY, query)
				.commit();
		}
		
		fragment.updateItems();
	}
	
	@Override
	public boolean onSearchRequested() {
		String query = PreferenceManager.getDefaultSharedPreferences(this)
					   .getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
		if(query == null) {
			return super.onSearchRequested();
		}
		else {
			startSearch(query, true, null, false);
			return true;
		}
	}

	

}
