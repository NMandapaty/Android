package com.bignerdranch.android.photogallery;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PollService extends IntentService {
	
	public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
	public static final String PREF_IS_ALARM_ON = "is alarm on";
	public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";
	
	public static final String EXTRA_REQUEST_CODE = "request code";
	public static final String EXTRA_NOTIFICATION = "notification";

	private static final String TAG = "PollService"; 
	
	private static final int POLL_INTERVAL = 5 * 60 * 1000; //5 minutes
	
	public PollService() {
		super(TAG);
	}
	
	void showBackgroundNotification(int requestCode, Notification notification) {
		Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
		i.putExtra(EXTRA_REQUEST_CODE, requestCode)
		 .putExtra(EXTRA_NOTIFICATION, notification);
		
		sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//Check connectivity
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		if(! isNetworkAvailable) {
			return;
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
		String lastResultID = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);
		
		ArrayList<GalleryItem> items = query != null ? 
				new FlickrFetchr().search(query) : 
				new FlickrFetchr().fetchItems();
				
		if(items.size() == 0) {
			return;
		}
		
		String resultID = items.get(0).getID();
		
		if(! resultID.equals(lastResultID)) {
			Log.i(TAG, "Got a new result: " + resultID);
			
			Resources r = getResources();
			Intent i = new Intent(this, PhotoGalleryActivity.class);
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
			
			Notification notification = new NotificationCompat.Builder(this)
				.setTicker(r.getString(R.string.new_pictures_title))
				.setSmallIcon(android.R.drawable.ic_menu_report_image)
				.setContentTitle(r.getString(R.string.new_pictures_title))
				.setContentText(r.getString(R.string.new_pictures_text))
				.setContentIntent(pi)
				.setAutoCancel(true)
				.build();
			
			showBackgroundNotification(0, notification);
		} else {
			Log.i(TAG, "Got an old result: " + resultID);
		}
		
		prefs.edit()
			.putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultID)
			.commit();
	}
	
	public static void setServiceAlarm(Context context, boolean turnOn) {
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		if(turnOn) {
			am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
		} else {
			am.cancel(pi);
			pi.cancel();
		}
		
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putBoolean(PREF_IS_ALARM_ON, turnOn)
			.commit();
	}
	
	public static boolean isServiceAlarmOn(Context context) {
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}

}
