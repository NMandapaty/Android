package com.bignerdranch.android.photogallery;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoGalleryFragment extends VisibleFragment {
	private static final String TAG = "PhotoGalleryFragment";
	private static final String SHORT_URL = "https://flic.kr/p/";
	
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		updateItems();
		
		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {

			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
				if(isVisible()) {
					imageView.setImageBitmap(thumbnail);
				}
			}
			
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		
		Log.i(TAG, "Background thread started");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, parent, false);
		
		mGridView = (GridView) v.findViewById(R.id.fragment_photo_gallery_gridView);
		
		setupAdapter(); 
		
		return v;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//Pull out the SearchView
			MenuItem searchItem = menu.findItem(R.id.menu_item_search);
			SearchView searchView = (SearchView) searchItem.getActionView();
			
			//Get the data from our searchable.xml as a SearchableInfo
			SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			ComponentName name = getActivity().getComponentName();
			SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
			
			searchView.setSearchableInfo(searchInfo);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_item_search:
			getActivity().onSearchRequested();
			return true;
		case R.id.menu_item_clear:
			PreferenceManager.getDefaultSharedPreferences(getActivity())
				.edit()
				.putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
				.commit();
			updateItems();
			return true;
		case R.id.menu_item_toggle_polling:
			boolean isAlarmOn = PollService.isServiceAlarmOn(getActivity());
			PollService.setServiceAlarm(getActivity(), ! isAlarmOn);
			
			Toast.makeText(getActivity(), 
						   isAlarmOn ? R.string.stop_polling_text : R.string.start_polling_text, 
						   Toast.LENGTH_SHORT)
				.show();
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getActivity().invalidateOptionsMenu();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		MenuItem toggleAlarm = menu.findItem(R.id.menu_item_toggle_polling);
		if (PollService.isServiceAlarmOn(getActivity())) {
			toggleAlarm.setTitle(R.string.stop_polling_title);
		} else {
			toggleAlarm.setTitle(R.string.start_polling_title);
		}
	}
	
	public void updateItems() {
		new FetchItemsTask().execute();
	}

    void setupAdapter() {
		if(getActivity() == null || mGridView == null) {
			return;
		}
		
		if(mItems != null)  {
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
		} else {
			mGridView.setAdapter(null);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	@Override 
	public void onDestroy() {
		super.onDestroy();
		mThumbnailThread.quit();
		
		Log.i(TAG, "Background thread destroyed");
	}
	
	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
		
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			Activity activity = getActivity();
			if(activity == null) {
				return new ArrayList<GalleryItem>();
			}
			
			String query = PreferenceManager.getDefaultSharedPreferences(activity)
					.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
			
			if(query != null) {
				return new FlickrFetchr().search(query);
			}
			else {
				return new FlickrFetchr().fetchItems();
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			mItems = items;
			setupAdapter();
		}
		
	}
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(), 0, items);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
			}
			
			final GalleryItem item = getItem(position);
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.img_loading);
			imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d(TAG, "item id = " + item.getID());
					String digits = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
					long ID = Long.parseLong(item.getID());
					Uri url = Uri.parse(SHORT_URL).buildUpon()
								.appendPath(FlickrFetchr.base_encode(ID, digits))
								.build();
					Intent i = new Intent(Intent.ACTION_VIEW, url);
					startActivity(i);
				}
			});
			
			mThumbnailThread.queueThumbnail(imageView, item.getURL());
			
			for(int i = 0; i < 5; i++) {
				int itemPosition = position + i + 1;
				if(itemPosition > parent.getChildCount() - 1) {
					break;
				}
				GalleryItem nextItem = getItem(position + i + 1);
				mThumbnailThread.preload(nextItem.getURL());
			}
			
			return convertView;
		}
	}

}
