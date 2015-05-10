package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimePickerFragment extends DialogFragment 
	implements OnClickListener, OnTimeChangedListener {
	
	public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
	
	private Calendar mTime;
	
	public static TimePickerFragment newInstance(Calendar date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TIME, date);
		
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mTime = (Calendar) getArguments().getSerializable(EXTRA_TIME);
		int hour = mTime.get(Calendar.HOUR_OF_DAY);
		int minute = mTime.get(Calendar.MINUTE);
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
		
		TimePicker timePicker = (TimePicker) v.findViewById(R.id.dialog_time_timePicker);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		timePicker.setOnTimeChangedListener(this);
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.time_picker_title)
			.setPositiveButton(android.R.string.ok, this)
			.create();
	}

	@Override
	/**
	 * Called when the time on the timePicker is changed
	 */
	public void onTimeChanged(TimePicker view, int hour, int minute) {
		mTime.set(Calendar.HOUR_OF_DAY, hour);
		mTime.set(Calendar.MINUTE, minute);
		
		getArguments().putSerializable(EXTRA_TIME, mTime);
	}

	@Override
	/**
	 * Called when the user clicks the "OK" button
	 */
	public void onClick(DialogInterface dialog, int which) {
		if(getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(EXTRA_TIME, mTime);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
	}

}
